package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.CommonRsListPersonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.exception.FriendShipNotFoundExeption;
import ru.skillbox.socialnet.exception.PersonNotFoundExeption;
import ru.skillbox.socialnet.service.FriendShipService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendsController {

    private final FriendShipService friendShipService;

    @PostMapping("/{id}")
    public CommonRs<ComplexRs> sendFriendshipRequest(@RequestHeader(name = "authorization") String authorization,
                                                     @PathVariable(name = "id") Long id)
            throws PersonNotFoundExeption, FriendShipNotFoundExeption {
        return friendShipService.sendFriendshipRequest(id, authorization);
    }

    @DeleteMapping("/{id}")
    public CommonRs<ComplexRs> deleteFriendById(@RequestHeader(name = "authorization") String authorization,
                                                @PathVariable(name = "id")  Long id)
            throws PersonNotFoundExeption, FriendShipNotFoundExeption {
        return friendShipService.deleteFriendById(id, authorization);
    }

    @PostMapping("/request/{id}")
    public CommonRs<ComplexRs> addFriendById(@RequestHeader(name = "authorization") String authorization,
                                             @PathVariable(name = "id") Long id)
            throws PersonNotFoundExeption, FriendShipNotFoundExeption {
        return friendShipService.addFriendById(id, authorization);
    }

    @DeleteMapping("/request/{id}")
    public CommonRs<ComplexRs> declineFriendshipRequestById(@RequestHeader(name = "authorization") String authorization,
                                                            @PathVariable(name = "id") Long id)
            throws PersonNotFoundExeption, FriendShipNotFoundExeption {
        return friendShipService.declineFriendshipRequestById(id, authorization);
    }

    @PostMapping("/block_unblock/{id}")
    public void blockOrUnblockUserByUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                      @PathVariable(name = "id") Long id)  throws PersonNotFoundExeption {
        friendShipService.blockOrUnblockUserByUser(id, authorization);
    }

    @GetMapping("")
    public CommonRsListPersonRs<PersonRs> getFriendsOfCurrentUser(@RequestHeader(name = "authorization") String authorization,
                                                                  @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                                  @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return  friendShipService.getFriendsOfCurrentUser(authorization, offset, perPage);
    }

    @GetMapping("/request")
    public CommonRsListPersonRs<PersonRs> getPotentialFriendsOfCurrentUser(@RequestHeader(name = "authorization") String authorization,
                                                                           @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                                           @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return friendShipService.getPotentialFriendsOfCurrentUser(authorization, offset, perPage);
    }

    @GetMapping("/recommendations")
    public CommonRsListPersonRs<PersonRs> getRecommendationFriends(@RequestHeader(name = "authorization") String authorization) {
        return friendShipService.getRecommendationFriends(authorization);
    }

    @GetMapping("/outgoing_requests")
    public CommonRsListPersonRs<PersonRs> getOutgoingRequestsByUser(@RequestHeader(name = "authorization") String authorization,
                                                       @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                       @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage)
            throws BadRequestException {
        return friendShipService.getOutgoingRequestsByUser(authorization, offset, perPage);
    }
}
