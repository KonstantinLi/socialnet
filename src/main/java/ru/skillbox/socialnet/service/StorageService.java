package ru.skillbox.socialnet.service;

import jakarta.servlet.MultipartConfigElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnet.dto.AwsS3Handler;
import ru.skillbox.socialnet.dto.UserRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.entity.other.Storage;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.repository.StorageRepository;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final AwsS3Handler awsS3Handler;

    private final JwtTokenUtils jwtTokenUtils;

    private final StorageRepository storageRepository;

    private final PersonService personService;

    @Value("${aws.max-file-size}")
    private String maxFileSize;

    @Value("${aws.photo-url-prefix}")
    private String photoURLPrefix;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        long maxFileSize = getMaxFileSize();
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));
        factory.setMaxRequestSize(DataSize.ofBytes(maxFileSize));
        return factory.createMultipartConfig();
    }

    public CommonRs<Storage> uploadProfileImage(String type, MultipartFile file) throws BadRequestException, InterruptedException {
        //TODO userID should be taken from token
        //Long userId = jwtTokenUtils.getId(token);
        long userId = 13L;

        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.warn("details: {}", details);

        String generateFileName = generateFileName(
                userId, Objects.requireNonNull(file.getOriginalFilename()));

        Storage storage = new Storage();
        storage.setCreatedAt(LocalDateTime.now());
        storage.setOwnerId(userId);
        storage.setFileSize(file.getSize());
        storage.setFileName(photoURLPrefix + generateFileName);
        storage.setFileType(type);

        awsS3Handler.uploadFile(type, file, generateFileName);

        Storage savedStorage = storageRepository.save(storage);

        CommonRs<Storage> response = new CommonRs<>();
        response.setData(savedStorage);

        updateUsersPhotoId(generateFileName, userId);

        return response;
    }

    private void updateUsersPhotoId(String generateFileName, long userId) {
        UserRq userRq = new UserRq();
        userRq.setPhoto_id(photoURLPrefix + generateFileName);
        personService.updateUserInfo(userId, userRq);
    }

    private String generateFileName(long userId, String originalFilename) {
        String[] split = originalFilename.split("\\.");
        String extension = split[split.length - 1];
        return userId + "_" + System.currentTimeMillis() + "." + extension;
    }

    private Long getMaxFileSize() {
        return Long.parseLong(maxFileSize);
    }
}
