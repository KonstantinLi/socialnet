package ru.skillbox.socialnet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendsController {

    @PostMapping("/{id}")
    public ResponseEntity<?> sendFriendshipRequest(@RequestHeader(name = "authorization") String authorization,
                                                   @PathVariable(name = "id") int id) {
        return ResponseEntity. ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>  deleteFriendById(@RequestHeader(name = "authorization") String authorization,
                                               @PathVariable(name = "id") int id) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request/{id}")
    public ResponseEntity<?> addFriendById(@RequestHeader(name = "authorization") String authorization,
                                           @PathVariable(name = "id") int id) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/request/{id}")
    public ResponseEntity<?> declineFriendshipRequestById(@RequestHeader(name = "authorization") String authorization,
                                                          @PathVariable(name = "id") int id) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block_unblock/{id}")
    public ResponseEntity<?> blockOrUnblockUserByUser(@RequestHeader(name = "authorization") String authorization,
                                                      @PathVariable(name = "id") int id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<?> getFriendsOfCurrentUser(@RequestHeader(name = "authorization") String authorization,
                                        @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                        @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/request")
    public ResponseEntity<?> getPotentialFriendsOfCurrentUser(@RequestHeader(name = "authorization") String authorization,
                                                     @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                     @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendationFriends(@RequestHeader(name = "authorization") String authorization) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/outgoing_requests")
    public ResponseEntity<?> getOutgoingRequestsByUser(@RequestHeader(name = "authorization") String authorization,
                                                       @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                       @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return ResponseEntity.ok().build();
    }
}
