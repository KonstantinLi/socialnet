package ru.skillbox.socialnet.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendsController {

    @PostMapping("/{id}")
    public ResponseEntity<?> sendFriendshipRequest(@RequestHeader(name = "authorization", required = true) String authorization,
                                                   @PathVariable(name = "id") int id) {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>  deleteFriendById(@RequestHeader(name = "authorization", required = true) String authorization,
                                               @PathVariable(name = "id") int id) {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/request/{id}")
    public ResponseEntity<?> addFriendById(@RequestHeader(name = "authorization", required = true) String authorization,
                                           @PathVariable(name = "id") int id) {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/request/{id}")
    public ResponseEntity<?> declineFriendshipRequestById(@RequestHeader(name = "authorization", required = true) String authorization,
                                                          @PathVariable(name = "id") int id) {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/block_unblock/{id}")
    public ResponseEntity<?> blockOrUnblockUserByUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                      @PathVariable(name = "id") int id) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("")
    public ResponseEntity<?> getFriendsOfCurrentUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                        @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                        @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/request")
    public ResponseEntity<?> getPotentialFriendsOfCurrentUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                     @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                     @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendationFriends(@RequestHeader(name = "authorization", required = true) String authorization) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/outgoing_requests")
    public ResponseEntity<?> getOutgoingRequestsByUser(@RequestHeader(name = "authorization", required = true) String authorization,
                                                       @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                       @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {
        return ResponseEntity.ok(null);
    }
}
