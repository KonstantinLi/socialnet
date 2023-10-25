package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.entity.other.Storage;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.service.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @ApiResponse(responseCode = "200")
    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public CommonRs<Storage> uploadProfileImage(
            @RequestParam("type") @Parameter(example = "IMAGE") String type,
            @RequestBody MultipartFile file)
            throws BadRequestException, IOException {

        return storageService.uploadProfileImage(type, file);
    }
}
