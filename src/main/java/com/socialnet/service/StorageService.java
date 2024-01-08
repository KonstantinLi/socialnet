package com.socialnet.service;

import com.socialnet.dto.ProfileImageManager;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.socialnet.entity.other.Storage;
import com.socialnet.exception.FileNotProvidedException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final ProfileImageManager profileImageManager;
    private final StorageRepository storageRepository;
    private final PersonService personService;

    @Value("${aws.photo-url-prefix}")
    private String photoURLPrefix;

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

        profileImageManager.uploadProfileImage(type, file, generateFileName);

        Storage savedStorage = storageRepository.save(storage);

        CommonRs<Storage> response = new CommonRs<>();
        response.setData(savedStorage);

        updateUsersPhotoId(generateFileName, userId);

        return response;
    }

    public void deleteProfileImage(long userId) {
        profileImageManager.deleteProfileImage(userId);
    }

    private void updateUsersPhotoId(String generateFileName, long userId) {
        personService.updateUserPhoto(userId, photoURLPrefix + generateFileName);
    }

    private String generateFileName(long userId, String originalFilename) {
        String[] split = originalFilename.split("\\.");
        String extension = split[split.length - 1];
        return userId + "_" + System.currentTimeMillis() + "." + extension;
    }
}
