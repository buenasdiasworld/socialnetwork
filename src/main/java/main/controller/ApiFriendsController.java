package main.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.response.FriendsResponse;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import main.service.FriendsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = { SpringFoxConfig.FRIEND_TAG })
@RestController
@RequiredArgsConstructor
public class ApiFriendsController {
    private final FriendsServiceImpl friendsService;

    @GetMapping(value = "/api/v1/friends")
    public ResponseEntity<FriendsResponse> friends(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int itemPerPage) {
        return ResponseEntity.ok(friendsService.getFriends(name, offset, itemPerPage));
    }

    @DeleteMapping(value = "/api/v1/friends/{id}")
    public ResponseEntity<FriendsResponse> removeFriend(@PathVariable int id) {
        return ResponseEntity.ok(friendsService.deleteFriend(id));
    }

    @GetMapping(value = "/api/v1/friends/recommendations")
    public ResponseEntity<FriendsResponse> recommendationFriends(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int itemPerPage) {
        return ResponseEntity.ok(friendsService.getRecommendations(offset, itemPerPage));
    }

    @PostMapping(value = "/api/v1/friends/{id}")
    public ResponseEntity<FriendsResponse> addFriend(@PathVariable int id) {
        return ResponseEntity.ok(friendsService.addFriend(id));
    }

    @GetMapping(value = "/api/v1/friends/request")
    public ResponseEntity<FriendsResponse> getRequests(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int itemPerPage) {
        return ResponseEntity.ok(friendsService.getRequests(offset, itemPerPage));
    }
}
