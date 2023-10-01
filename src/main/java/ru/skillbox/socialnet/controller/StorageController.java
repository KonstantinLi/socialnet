package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.entity.other.Storage;
import ru.skillbox.socialnet.service.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping()
    public CommonRs<Storage> uploadProfileImage(@RequestParam("type") String type,
                                                @RequestBody MultipartFile file)
            throws IOException {

        return storageService.uploadProfileImage(type, file);
    }
}
