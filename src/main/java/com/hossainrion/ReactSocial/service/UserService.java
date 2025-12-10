package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.dto.forUser.*;
import com.hossainrion.ReactSocial.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    FriendStatus getFriendStatus(User otherUser, User currentUser);
    Boolean usernameExists(String username);
    User getUserById(Long id);
    ResponseEntity<UserResponseDto> getUser(HttpServletRequest request);
    User getCurrentUser(HttpServletRequest request);
    List<User> getAllUsers();
    Boolean addUser(UserSaveDto userSaveDto);
    ResponseEntity<Boolean> updateUser(UserUpdateDto userUpdateDto, HttpServletRequest request);
    ResponseEntity<?> handleAuthentication(LoginDto loginDto);
    User getUserByUsername(String username);
    ResponseEntity<Boolean> addFriend(Long id, HttpServletRequest request);
    ResponseEntity<Boolean> unfriend(Long id, HttpServletRequest request);
    ResponseEntity<Boolean> cancelFriendRequest(Long id, HttpServletRequest request);
    ResponseEntity<Boolean> cancelReceivedRequest(Long id, HttpServletRequest request);
    ResponseEntity<Boolean> acceptFriendRequest(Long id, HttpServletRequest request);
    ResponseEntity<List<UserResponseDto>> getSentRequests(HttpServletRequest request);;
    List<User> getReceivedRequestsById(Long id);
    ResponseEntity<List<UserResponseDto>> getReceivedRequests(HttpServletRequest request);
    ResponseEntity<List<UserResponseDto>> getFriends(HttpServletRequest request);
    ResponseEntity<List<UserResponseDto>> getFriendsSuggestion(HttpServletRequest request);
}
