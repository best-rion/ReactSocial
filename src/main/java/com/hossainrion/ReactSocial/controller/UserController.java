package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.dto.IdDto;
import com.hossainrion.ReactSocial.dto.UserResponseDto;
import com.hossainrion.ReactSocial.dto.UserSaveDto;
import com.hossainrion.ReactSocial.dto.UserUpdateDto;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {this.userService = userService;}

    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getUser(request));
    }

    @PostMapping
    public ResponseEntity<Boolean> saveUser(@RequestBody UserSaveDto userSaveDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(userSaveDto));
    }

    @PutMapping
    public ResponseEntity<Boolean> updateUser(@RequestBody UserUpdateDto userUpdateDto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.updateUser(userUpdateDto, request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream().map(User::getUsername).toList());
    }

    @GetMapping("/friends-suggestion")
    public ResponseEntity<List<UserResponseDto>> getFriendsSuggestion(HttpServletRequest request) {
        List<User> users = userService.getAllUsers();
        User thisUser = userService.getUserByEmail(JwtUtil.getEmailFromRequest(request));
        users.remove(thisUser);
        users.removeAll(thisUser.getSentRequests());
        users.removeAll(userService.getReceivedRequestsById(thisUser.getId())); // TODO: need to move this logic to UserRepository
        users.removeAll(thisUser.getFriends());
        users.sort((a,b)->Long.compare(b.getId(),a.getId()));
        return ResponseEntity.ok(users.stream().map(UserResponseDto::fromUser).toList());
    }

    @PutMapping("add-friend")
    public ResponseEntity<Boolean> addFriend(@RequestBody IdDto userIdDto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.addFriend(userIdDto.id(), request));
    }

    @GetMapping("sent-requests")
    public ResponseEntity<List<UserResponseDto>> getSentRequests(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getSentRequests(request));
    }

    @GetMapping("received-requests")
    public ResponseEntity<List<UserResponseDto>> getReceivedRequests(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getReceivedRequests(request));
    }

    @PutMapping("accept-request")
    public ResponseEntity<Boolean> acceptRequest(@RequestBody IdDto userIdDto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.acceptFriendRequest(userIdDto.id(), request));
    }

    @GetMapping("friends")
    public ResponseEntity<List<UserResponseDto>> getFriends(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getFriends(request));
    }

    @PutMapping("unfriend")
    public ResponseEntity<Boolean> unfriend(@RequestBody IdDto userIdDto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.unfriend(userIdDto.id(), request));
    }

    @PutMapping("cancel-request")
    public ResponseEntity<Boolean> cancelRequest(@RequestBody IdDto userIdDto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.cancelFriendRequest(userIdDto.id(), request));
    }

    @PutMapping("cancel-received-request")
    public ResponseEntity<Boolean> cancelReceivedRequest(@RequestBody IdDto userIdDto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.cancelReceivedRequest(userIdDto.id(), request));
    }
}
