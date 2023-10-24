package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.OnlineStatusUpdate;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.exception.FriendShipNotFoundException;
import ru.skillbox.socialnet.exception.PersonNotFoundException;
import ru.skillbox.socialnet.service.FriendShipService;

import java.util.List;

@Tag(name = "FriendsController", description = "Get recommended or potential friends. Add, delete, get friends. Send, delete friendship request")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendsController {

    private final FriendShipService friendShipService;

    @Operation(summary = "send friendship request by id of another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    ref = "#/components/schemas/CommonRsComplexRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)})
    @OnlineStatusUpdate
    @PostMapping("/{id}")
    public CommonRs<ComplexRs> sendFriendshipRequest(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Token",
                    required = true) String authorization,
            @PathVariable(name = "id") @Parameter(description = "id", example = "1", required = true) Long id)
            throws PersonNotFoundException {
        return friendShipService.sendFriendshipRequest(id, authorization);
    }

    @Operation(summary = "delete friend by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    ref = "#/components/schemas/CommonRsComplexRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"error\": \"FriendShipNotFoundException\",\n" +
                                    "  \"timestamp\": 12432857239,\n" +
                                    "  \"error_description\": \"Запись о дружбе не найдена\"\n" +
                                    "}"),
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)})
    @OnlineStatusUpdate
    @DeleteMapping("/{id}")
    public CommonRs<ComplexRs> deleteFriendById(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Tokenn",
                    required = true) String authorization,
            @PathVariable(name = "id") @Parameter(description = "id", example = "1", required = true) Long id)
            throws PersonNotFoundException, FriendShipNotFoundException {
        return friendShipService.deleteFriendById(id, authorization);
    }

    @Operation(summary = "add friend by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    ref = "#/components/schemas/CommonRsComplexRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)})
    @OnlineStatusUpdate
    @PostMapping("/request/{id}")
    public CommonRs<ComplexRs> addFriendById(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Tokenn",
                    required = true) String authorization,
            @PathVariable(name = "id") @Parameter(description = "id", example = "1", required = true) Long id)
            throws PersonNotFoundException, FriendShipNotFoundException {
        return friendShipService.addFriendById(id, authorization);
    }

    @Operation(summary = "decline friendship request by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    ref = "#/components/schemas/CommonRsComplexRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)})
    @OnlineStatusUpdate
    @DeleteMapping("/request/{id}")
    public CommonRs<ComplexRs> declineFriendshipRequestById(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Tokenn",
                    required = true) String authorization,
            @PathVariable(name = "id") @Parameter(description = "id", example = "1", required = true) Long id)
            throws PersonNotFoundException, FriendShipNotFoundException {
        return friendShipService.declineFriendshipRequestById(id, authorization);
    }

    @Operation(summary = "block or unblock (if user in block) user by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "/",
                            schema = @Schema(implementation = HttpStatus.class
                            ))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response",
                                    implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)})
    @OnlineStatusUpdate
    @PostMapping("/block_unblock/{id}")
    public void blockOrUnblockUserByUser(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Tokenn",
                    required = true) String authorization,
            @PathVariable(name = "id") @Parameter(description = "id", example = "1", required = true) Long id)
            throws PersonNotFoundException {
        friendShipService.blockOrUnblockUserByUser(id, authorization);
    }

    @Operation(summary = "get friends of current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    ref = "#/components/schemas/CommonRsListPersonRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response",
                                    implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)})
    @OnlineStatusUpdate
    @GetMapping("")
    public CommonRs<List<PersonRs>> getFriendsOfCurrentUser(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Tokenn",
                    required = true) String authorization,
            @RequestParam(name = "offset", required = false, defaultValue = "0")
            @Parameter(description = "offset", example = "0") int offset,
            @RequestParam(name = "perPage", required = false, defaultValue = "20")
            @Parameter(description = "perPage", example = "20") int perPage)
            throws PersonNotFoundException {
        return friendShipService.getFriendsOfCurrentUser(authorization, offset, perPage);
    }

    @Operation(summary = "get potential friends of current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    ref = "#/components/schemas/CommonRsListPersonRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response",
                                    implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)})
    @OnlineStatusUpdate
    @GetMapping("/request")
    public CommonRs<List<PersonRs>> getPotentialFriendsOfCurrentUser(
            @RequestHeader(name = "authorization") String authorization,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage)
            throws PersonNotFoundException {

        return friendShipService.getPotentialFriendsOfCurrentUser(authorization, offset, perPage);
    }

    @Operation(summary = "get recommendation friends")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    ref = "#/components/schemas/CommonRsListPersonRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response",
                                    implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)})
    @OnlineStatusUpdate
    @GetMapping("/recommendations")
    public CommonRs<List<PersonRs>> getRecommendationFriends(
            @RequestHeader(name = "authorization") String authorization)
            throws PersonNotFoundException {
        return friendShipService.getRecommendationFriends(authorization);
    }

    @Operation(summary = "get outgoing requests by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    ref = "#/components/schemas/CommonRsListPersonRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response",
                                    implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)})
    @OnlineStatusUpdate
    @GetMapping("/outgoing_requests")
    public CommonRs<List<PersonRs>> getOutgoingRequestsByUser(
            @RequestHeader(name = "authorization") String authorization,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage)
            throws PersonNotFoundException {
        return friendShipService.getOutgoingRequestsByUser(authorization, offset, perPage);
    }
}
