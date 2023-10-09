package ru.skillbox.socialnet.service;

import jakarta.servlet.MultipartConfigElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnet.dto.AwsS3Handler;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.entity.other.Storage;
import ru.skillbox.socialnet.exception.FileNotProvidedException;
import ru.skillbox.socialnet.repository.StorageRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final AwsS3Handler awsS3Handler;
    private final StorageRepository storageRepository;
    private final PersonService personService;

    @Value("${aws.max-image-file-size}")
    private String maxImageFileSize;

    @Value("${aws.photo-url-prefix}")
    private String photoURLPrefix;


    //TODO move to config
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        long maxFileSize = getMaxImageFileSize();
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));
        factory.setMaxRequestSize(DataSize.ofBytes(maxFileSize));
        return factory.createMultipartConfig();
    }

    public CommonRs<Storage> uploadProfileImage(String type, MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new FileNotProvidedException("File is empty or not provided");
        }

        String userIdString = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName()
                .split(",")[0];
        long userId = Long.parseLong(userIdString);

        String generateFileName = generateFileName(
                userId, Objects.requireNonNull(file.getOriginalFilename()));

        Storage storage = new Storage();
        storage.setCreatedAt(LocalDateTime.now());
        storage.setOwnerId(userId);
        storage.setFileSize(file.getSize());
        storage.setFileName(photoURLPrefix + generateFileName);
        storage.setFileType(type);

        awsS3Handler.uploadImage(type, file, generateFileName);

        Storage savedStorage = storageRepository.save(storage);

        CommonRs<Storage> response = new CommonRs<>();
        response.setData(savedStorage);

        updateUsersPhotoId(generateFileName, userId);

        return response;
    }

    private void updateUsersPhotoId(String generateFileName, long userId) {
        personService.updateUserPhoto(userId, photoURLPrefix + generateFileName);
    }

    private String generateFileName(long userId, String originalFilename) {
        String[] split = originalFilename.split("\\.");
        String extension = split[split.length - 1];
        return userId + "_" + System.currentTimeMillis() + "." + extension;
    }

    private Long getMaxImageFileSize() {
        return Long.parseLong(maxImageFileSize);
    }
}
