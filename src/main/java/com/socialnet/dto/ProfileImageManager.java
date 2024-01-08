package com.socialnet.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileImageManager {
    void uploadProfileImage(String type, MultipartFile image, String fileName) throws IOException;

    void deleteProfileImage(Long userId);
}
