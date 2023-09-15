package ru.skillbox.socialnet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.response.ApiFatherRs;
import ru.skillbox.socialnet.dto.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.services.FriendShipService;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendsController {

    @Autowired
    FriendShipService friendShipService;

    private ResponseEntity<?> generateResponseEntity(ApiFatherRs response) {
        if (response == null) {
            //block/unblock correct response is null
          return ResponseEntity.ok().build();
        } else if (response instanceof ErrorRs){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
             return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> sendFriendshipRequest(@RequestHeader(name = "authorization", required = true) String authorization,
                                                   @PathVariable(name = "id") int id) {
        ApiFatherRs response = friendShipService.sendFriendshipRequest(id, authorization);
        return generateResponseEntity(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>  deleteFriendById(@RequestHeader(name = "authorization", required = true) String authorization,
                                               @PathVariable(name = "id") int id) {
        ApiFatherRs response = friendShipService.deleteFriendById(id, authorization);
        return generateResponseEntity(response);
    }

    @PostMapping("/request/{id}")
    public ResponseEntity<?> addFriendById(@RequestHeader(name = "authorization", required = true) String authorization,
                                           @PathVariable(name = "id") int id) {
        ApiFatherRs response = friendShipService.addFriendById(id, authorization);
        return generateResponseEntity(response);
    }

    @DeleteMapping("/request/{id}")
    public ResponseEntity<?> declineFriendshipRequestById(@RequestHeader(name = "authorization", required = true) String authorization,
                                                          @PathVariable(name = "id") int id) {
        ApiFatherRs response = friendShipService.declineFriendshipRequestById(id, authorization);
        return generateResponseEntity(response);
    }

    @PostMapping("/block_unblock/{id}")
    public ResponseEntity<?> blockOrUnblockUserByUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                      @PathVariable(name = "id") int id) {
        ApiFatherRs response = friendShipService.blockOrUnblockUserByUser(id, authorization);
        return generateResponseEntity(response);
    }

    @GetMapping("")
    public ResponseEntity<?> getFriendsOfCurrentUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                        @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                        @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        ApiFatherRs response = friendShipService.getFriendsOfCurrentUser(authorization, offset, perPage);
        return generateResponseEntity(response);
    }

    @GetMapping("/request")
    public ResponseEntity<?> getPotentialFriendsOfCurrentUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                     @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                     @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        ApiFatherRs response = friendShipService.getPotentialFriendsOfCurrentUser(authorization, offset, perPage);
        return generateResponseEntity(response);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendationFriends(@RequestHeader(name = "authorization", required = true) String authorization) {
        ApiFatherRs response = friendShipService.getRecommendationFriends(authorization);
        return generateResponseEntity(response);
    }

    @GetMapping("/outgoing_requests")
    public ResponseEntity<?> getOutgoingRequestsByUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                       @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                       @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        ApiFatherRs response = friendShipService.getOutgoingRequestsByUser(authorization, offset, perPage);
        return generateResponseEntity(response);
    }
}
