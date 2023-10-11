package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.aspect.OnlineStatusUpdate;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.exception.person.FriendShipNotFoundException;
import ru.skillbox.socialnet.exception.person.PersonNotFoundException;
import ru.skillbox.socialnet.service.FriendShipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendsController {

    private final FriendShipService friendShipService;

    @OnlineStatusUpdate
    @PostMapping("/{id}")
    public CommonRs<ComplexRs> sendFriendshipRequest(
            @RequestHeader(name = "authorization") String authorization,
            @PathVariable(name = "id") Long id)
            throws PersonNotFoundException {

        return friendShipService.sendFriendshipRequest(id, authorization);
    }

    @OnlineStatusUpdate
    @DeleteMapping("/{id}")
    public CommonRs<ComplexRs> deleteFriendById(
            @RequestHeader(name = "authorization") String authorization,
            @PathVariable(name = "id") Long id)
            throws PersonNotFoundException, FriendShipNotFoundException {

        return friendShipService.deleteFriendById(id, authorization);
    }

    @OnlineStatusUpdate
    @PostMapping("/request/{id}")
    public CommonRs<ComplexRs> addFriendById(
            @RequestHeader(name = "authorization") String authorization,
            @PathVariable(name = "id") Long id)
            throws PersonNotFoundException, FriendShipNotFoundException {

        return friendShipService.addFriendById(id, authorization);
    }

    @OnlineStatusUpdate
    @DeleteMapping("/request/{id}")
    public CommonRs<ComplexRs> declineFriendshipRequestById(
            @RequestHeader(name = "authorization") String authorization,
            @PathVariable(name = "id") Long id)
            throws PersonNotFoundException, FriendShipNotFoundException {

        return friendShipService.declineFriendshipRequestById(id, authorization);
    }

    @OnlineStatusUpdate
    @PostMapping("/block_unblock/{id}")
    public void blockOrUnblockUserByUser(
            @RequestHeader(name = "authorization") String authorization,
            @PathVariable(name = "id") Long id) throws PersonNotFoundException {

        friendShipService.blockOrUnblockUserByUser(id, authorization);
    }

    @OnlineStatusUpdate
    @GetMapping("")
    public CommonRs<List<PersonRs>> getFriendsOfCurrentUser(
            @RequestHeader(name = "authorization") String authorization,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage)
            throws PersonNotFoundException {

        return friendShipService.getFriendsOfCurrentUser(authorization, offset, perPage);
    }

    @OnlineStatusUpdate
    @GetMapping("/request")
    public CommonRs<List<PersonRs>> getPotentialFriendsOfCurrentUser(
            @RequestHeader(name = "authorization") String authorization,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage)
            throws PersonNotFoundException {

        return friendShipService.getPotentialFriendsOfCurrentUser(authorization, offset, perPage);
    }

    @OnlineStatusUpdate
    @GetMapping("/recommendations")
    public CommonRs<List<PersonRs>> getRecommendationFriends(
            @RequestHeader(name = "authorization") String authorization)
            throws PersonNotFoundException {

        return friendShipService.getRecommendationFriends(authorization);
    }

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
