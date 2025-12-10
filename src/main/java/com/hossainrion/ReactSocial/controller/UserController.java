package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.dto.IdDto;
import com.hossainrion.ReactSocial.dto.forUser.FriendStatus;
import com.hossainrion.ReactSocial.dto.forUser.UserResponseDto;
import com.hossainrion.ReactSocial.dto.forUser.UserSaveDto;
import com.hossainrion.ReactSocial.dto.forUser.UserUpdateDto;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {this.userService = userService;}

    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(HttpServletRequest request) {
        return userService.getUser(request);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("userId") Long userId, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser(request);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User otherUser = userService.getUserById(userId);
        if (otherUser == null) return ResponseEntity.notFound().build();

        FriendStatus status = userService.getFriendStatus(otherUser, currentUser);
        return ResponseEntity.ok(UserResponseDto.fromUserWithFriendStatus(otherUser, status));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(UserResponseDto.fromUser(userService.getUserByUsername(username)));
    }

    @PostMapping
    public ResponseEntity<Boolean> saveUser(@RequestBody UserSaveDto userSaveDto) {
        if (userService.usernameExists(userSaveDto.username())) return ResponseEntity.status(HttpStatus.CONFLICT).body(false);

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(userSaveDto));
    }

    @GetMapping("/check-username/{username}")
    public Mono<ResponseEntity<Boolean>> checkUsername(@PathVariable("username") String username) {
        return Mono.just(ResponseEntity.ok(userService.usernameExists(username)));
    }

    @PutMapping
    public ResponseEntity<Boolean> updateUser(@RequestBody UserUpdateDto userUpdateDto, HttpServletRequest request) {
        return userService.updateUser(userUpdateDto, request);
    }

    @GetMapping("friends-suggestion")
    public ResponseEntity<List<UserResponseDto>> getFriendsSuggestion(HttpServletRequest request) {
        return userService.getFriendsSuggestion(request);
    }

    @PutMapping("add-friend")
    public ResponseEntity<Boolean> addFriend(@RequestBody IdDto userIdDto, HttpServletRequest request) {
        return userService.addFriend(userIdDto.id(), request);
    }

    @GetMapping("sent-requests")
    public ResponseEntity<List<UserResponseDto>> getSentRequests(HttpServletRequest request) {
        return userService.getSentRequests(request);
    }

    @GetMapping("received-requests")
    public ResponseEntity<List<UserResponseDto>> getReceivedRequests(HttpServletRequest request) {
        return userService.getReceivedRequests(request);
    }

    @PutMapping("accept-request")
    public ResponseEntity<Boolean> acceptRequest(@RequestBody IdDto userIdDto, HttpServletRequest request) {
        return userService.acceptFriendRequest(userIdDto.id(), request);
    }

    @GetMapping("friends")
    public ResponseEntity<List<UserResponseDto>> getFriends(HttpServletRequest request) {
        return userService.getFriends(request);
    }

    @PutMapping("unfriend")
    public ResponseEntity<Boolean> unfriend(@RequestBody IdDto userIdDto, HttpServletRequest request) {
        return userService.unfriend(userIdDto.id(), request);
    }

    @PutMapping("cancel-request")
    public ResponseEntity<Boolean> cancelRequest(@RequestBody IdDto userIdDto, HttpServletRequest request) {
        return userService.cancelFriendRequest(userIdDto.id(), request);
    }

    @PutMapping("reject-request")
    public ResponseEntity<Boolean> cancelReceivedRequest(@RequestBody IdDto userIdDto, HttpServletRequest request) {
        return userService.cancelReceivedRequest(userIdDto.id(), request);
    }
}
