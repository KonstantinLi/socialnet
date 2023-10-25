package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.ErrorAPIResponsesDescription;
import ru.skillbox.socialnet.annotation.OkAPIResponseDescription;
import ru.skillbox.socialnet.annotation.OnlineStatusUpdate;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.exception.FriendShipNotFoundException;
import ru.skillbox.socialnet.exception.PersonNotFoundException;
import ru.skillbox.socialnet.service.FriendShipService;

import java.util.List;

@Tag(name = "FriendsController", description = "Get recommended or potential friends. Add, delete, get friends. Send, delete friendship request")
@RestController
@RequiredArgsConstructor
@ErrorAPIResponsesDescription
@RequestMapping("/api/v1/friends")
public class FriendsController {

    private final FriendShipService friendShipService;

    @OkAPIResponseDescription(endpointDescription = "send friendship request by id of another user", value = "CommonRsComplexRs")
    @OnlineStatusUpdate
    @PostMapping("/{id}")
    public CommonRs<ComplexRs> sendFriendshipRequest(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Token",
                    required = true) String authorization,
            @PathVariable(name = "id") @Parameter(description = "id", example = "1", required = true) Long id)
            throws PersonNotFoundException {
        return friendShipService.sendFriendshipRequest(id, authorization);
    }

    @OkAPIResponseDescription(endpointDescription = "delete friend by id", value = "CommonRsComplexRs")
    @OnlineStatusUpdate
    @DeleteMapping("/{id}")
    public CommonRs<ComplexRs> deleteFriendById(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Tokenn",
                    required = true) String authorization,
            @PathVariable(name = "id") @Parameter(description = "id", example = "1", required = true) Long id)
            throws PersonNotFoundException, FriendShipNotFoundException {
        return friendShipService.deleteFriendById(id, authorization);
    }

    @OkAPIResponseDescription(endpointDescription = "add friend by id", value = "CommonRsComplexRs")
    @OnlineStatusUpdate
    @PostMapping("/request/{id}")
    public CommonRs<ComplexRs> addFriendById(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Tokenn",
                    required = true) String authorization,
            @PathVariable(name = "id") @Parameter(description = "id", example = "1", required = true) Long id)
            throws PersonNotFoundException, FriendShipNotFoundException {
        return friendShipService.addFriendById(id, authorization);
    }

    @OkAPIResponseDescription(endpointDescription = "decline friendship request by id", value = "CommonRsComplexRs")
    @OnlineStatusUpdate
    @DeleteMapping("/request/{id}")
    public CommonRs<ComplexRs> declineFriendshipRequestById(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Tokenn",
                    required = true) String authorization,
            @PathVariable(name = "id") @Parameter(description = "id", example = "1", required = true) Long id)
            throws PersonNotFoundException, FriendShipNotFoundException {
        return friendShipService.declineFriendshipRequestById(id, authorization);
    }

//        @OkAPIResponseDescription(endpointDescription = "block or unblock (if user in block) user by user id", value = "HttpStatus")
    @Operation(summary = "block or unblock (if user in block) user by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "/",
                            schema = @Schema(implementation = HttpStatus.class
                            ))})})
    @OnlineStatusUpdate
    @PostMapping("/block_unblock/{id}")
    public void blockOrUnblockUserByUser(
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Tokenn",
                    required = true) String authorization,
            @PathVariable(name = "id") @Parameter(description = "id", example = "1", required = true) Long id)
            throws PersonNotFoundException {
        friendShipService.blockOrUnblockUserByUser(id, authorization);
    }

    @OkAPIResponseDescription(endpointDescription = "get friends of current user", value = "CommonRsListPersonRs")
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

    @OkAPIResponseDescription(endpointDescription = "get potential friends of current user", value = "CommonRsListPersonRs")
    @OnlineStatusUpdate
    @GetMapping("/request")
    public CommonRs<List<PersonRs>> getPotentialFriendsOfCurrentUser(
            @RequestHeader(name = "authorization") String authorization,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage)
            throws PersonNotFoundException {

        return friendShipService.getPotentialFriendsOfCurrentUser(authorization, offset, perPage);
    }

    @OkAPIResponseDescription(endpointDescription = "get recommendation friends", value = "CommonRsListPersonRs")
    @OnlineStatusUpdate
    @GetMapping("/recommendations")
    public CommonRs<List<PersonRs>> getRecommendationFriends(
            @RequestHeader(name = "authorization") String authorization)
            throws PersonNotFoundException {
        return friendShipService.getRecommendationFriends(authorization);
    }

    @OkAPIResponseDescription(endpointDescription = "get outgoing requests by user", value = "CommonRsListPersonRs")
//    @Operation(summary = "get outgoing requests by user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = {@Content(mediaType = "application/json",
//                            schema = @Schema(
//                                    description = "default response from server",
//                                    ref = "#/components/schemas/CommonRsListPersonRs")
//                    )})})
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
