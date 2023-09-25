package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.entity.other.Storage;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.service.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    //TODO WHY DOESN'T WORK WITHOUT @Autowired
    @Autowired
    private StorageService storageService;

    @PostMapping()
    public CommonRs<Storage> uploadProfileImage(@RequestParam("type") String type,
                                                @RequestBody MultipartFile file) throws BadRequestException, InterruptedException {
        return storageService.uploadProfileImage(type, file);
    }
}
