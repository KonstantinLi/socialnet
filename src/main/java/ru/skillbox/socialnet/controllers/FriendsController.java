package ru.skillbox.socialnet.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.ComplexRs;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.response.CommonRsListPersonRs;
import ru.skillbox.socialnet.services.FriendShipService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendsController {
    FriendShipService friendShipService;

    @PostMapping("/{id}")
    public CommonRsComplexRs<ComplexRs> sendFriendshipRequest(@RequestHeader(name = "authorization", required = true) String authorization,
                                                   @PathVariable(name = "id") int id) {
        return friendShipService.sendFriendshipRequest(id, authorization);
    }

    @DeleteMapping("/{id}")
    public CommonRsComplexRs<ComplexRs>  deleteFriendById(@RequestHeader(name = "authorization", required = true) String authorization,
                                               @PathVariable(name = "id") int id) {
        return friendShipService.deleteFriendById(id, authorization);
    }

    @PostMapping("/request/{id}")
    public CommonRsComplexRs<ComplexRs> addFriendById(@RequestHeader(name = "authorization", required = true) String authorization,
                                           @PathVariable(name = "id") int id) {
        return friendShipService.addFriendById(id, authorization);
    }

    @DeleteMapping("/request/{id}")
    public CommonRsComplexRs<ComplexRs> declineFriendshipRequestById(@RequestHeader(name = "authorization", required = true) String authorization,
                                                          @PathVariable(name = "id") int id) {
        return friendShipService.declineFriendshipRequestById(id, authorization);
    }

    @PostMapping("/block_unblock/{id}")
    public ResponseEntity<?> blockOrUnblockUserByUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                      @PathVariable(name = "id") int id) {
        friendShipService.blockOrUnblockUserByUser(id, authorization);
        return ResponseEntity.ok(null);
    }

    @GetMapping("")
    public CommonRsListPersonRs<PersonRs> getFriendsOfCurrentUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                                  @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                                  @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return  friendShipService.getFriendsOfCurrentUser(authorization, offset, perPage);
    }

    @GetMapping("/request")
    public CommonRsListPersonRs<PersonRs> getPotentialFriendsOfCurrentUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                     @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                     @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return friendShipService.getPotentialFriendsOfCurrentUser(authorization, offset, perPage);
    }

    @GetMapping("/recommendations")
    public CommonRsListPersonRs<PersonRs> getRecommendationFriends(@RequestHeader(name = "authorization", required = true) String authorization) {
        return friendShipService.getRecommendationFriends(authorization);
    }

    @GetMapping("/outgoing_requests")
    public CommonRsListPersonRs<PersonRs> getOutgoingRequestsByUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                       @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                       @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return friendShipService.getOutgoingRequestsByUser(authorization, offset, perPage);
    }
}
