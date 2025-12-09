package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.dto.forUser.*;
import com.hossainrion.ReactSocial.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    FriendStatus getFriendStatus(User otherUser, HttpServletRequest request);
    Boolean usernameExists(String username);
    User getUserById(Long id);
    UserResponseDto getUser(HttpServletRequest request);
    User getCurrentUser(HttpServletRequest request);
    List<User> getAllUsers();
    Boolean addUser(UserSaveDto userSaveDto);
    Boolean updateUser(UserUpdateDto userUpdateDto, HttpServletRequest request);
    ResponseEntity<?> handleAuthentication(LoginDto loginDto);
    User getUserByUsername(String username);
    Boolean addFriend(Long id, HttpServletRequest request);
    List<UserResponseDto> getSentRequests(HttpServletRequest request);;
    List<User> getReceivedRequestsById(Long id);
    List<UserResponseDto> getReceivedRequests(HttpServletRequest request);
    Boolean acceptFriendRequest(Long id, HttpServletRequest request);
    List<UserResponseDto> getFriends(HttpServletRequest request);
    Boolean unfriend(Long id, HttpServletRequest request);
    Boolean cancelFriendRequest(Long id, HttpServletRequest request);
    Boolean cancelReceivedRequest(Long id, HttpServletRequest request);
    List<UserResponseDto> getFriendsSuggestion(HttpServletRequest request);
}
