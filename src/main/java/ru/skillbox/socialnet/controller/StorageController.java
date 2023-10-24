package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.entity.other.Storage;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.service.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;


    @Operation(summary = "upload users profile image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    //TODO check what is the correct answer
                                    ref = "#/components/schemas/CommonRsStorage")
                    )}),
            @ApiResponse(responseCode = "400", description = "name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response",
                                    implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)})
    @PostMapping()
    public CommonRs<Storage> uploadProfileImage(
            @RequestParam("type") @Parameter(description = "type of file", example = "IMAGE") String type,
            @RequestBody @Parameter(description = "file",
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(description = "file as MultipartFile",
                                    implementation = MultipartFile.class)))
            MultipartFile file)
            throws BadRequestException, IOException {

        return storageService.uploadProfileImage(type, file);
    }
}
