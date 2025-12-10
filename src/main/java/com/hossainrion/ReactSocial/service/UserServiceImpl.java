package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.dto.forUser.*;
import com.hossainrion.ReactSocial.utils.Util;
import com.hossainrion.ReactSocial.dto.*;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public User getCurrentUser(HttpServletRequest request) {
        return userRepository.findByUsername(JwtUtil.getUsernameFromRequest(request));
    }

    @Override
    public FriendStatus getFriendStatus(User otherUser, User currentUser) {
        if (currentUser.getFriends().contains(otherUser)) return FriendStatus.FRIEND;
        if (currentUser.getSentRequests().contains(otherUser)) return FriendStatus.SENT;
        if (otherUser.getSentRequests().contains(currentUser)) return FriendStatus.RECEIVED;
        return FriendStatus.UNKNOWN;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    @Transactional
    public Boolean addUser(UserSaveDto userSaveDto) {
        User user = new User();
        user.setFullName(userSaveDto.fullName());
        user.setUsername(userSaveDto.username());
        user.setPassword(userSaveDto.password());
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public ResponseEntity<Boolean> updateUser(UserUpdateDto userUpdateDto, HttpServletRequest request) {
        User user = getCurrentUser(request);
		if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        user.setFullName(userUpdateDto.fullName());
        user.setBio(userUpdateDto.bio());

        Util.deleteProfilePicture(user.getPicture());
        if (!userUpdateDto.pictureBase64().isEmpty()) {
            String fileName = Util.saveProfilePicture(userUpdateDto.pictureBase64());
            if (fileName != null) {
                user.setPicture(fileName);
            }
        }

        userRepository.save(user);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<?> handleAuthentication(LoginDto loginDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final UserDetails user = userDetailsService.loadUserByUsername(loginDto.username());
        final String jwt = JwtUtil.generateJwtToken(user.getUsername());
        final String refreshToken = JwtUtil.generateRefreshToken(user.getUsername());
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)       // must be false for HTTP localhost
                .sameSite("Lax")     // "None" requires secure=true
                .path("/")
                .maxAge(JwtUtil.REFRESH_TOKEN_EXPIRATION_TIME_IN_SECONDS)
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new JwtResponse(jwt, JwtUtil.JWT_TOKEN_EXPIRATION_TIME_IN_SECONDS * 1000));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public ResponseEntity<UserResponseDto> getUser(HttpServletRequest request) {
        User user = getCurrentUser(request);
		if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(UserResponseDto.fromUser(user));
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getSentRequests(HttpServletRequest request) {
        User user = getCurrentUser(request);
		if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(user.getSentRequests().stream().map(UserResponseDto::fromUser).toList());
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getReceivedRequests(HttpServletRequest request) {
        User user = getCurrentUser(request);
		if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(getReceivedRequestsById(user.getId()).stream().map(UserResponseDto::fromUser).toList());
    }

    @Override
    public List<User> getReceivedRequestsById(Long id) {
        return userRepository.findReceivedRequestsById(id);
    }

    @Override
    public ResponseEntity<Boolean> addFriend(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
		if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Set<User> sentRequests = user.getSentRequests();
        sentRequests.add(User.builder().id(id).build());
        user.setSentRequests(sentRequests);
        userRepository.save(user);
        return ResponseEntity.ok(true);
    }

    @Override
    @Transactional
    public ResponseEntity<Boolean> acceptFriendRequest(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
		if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User sender = userRepository.findById(id);
        if (!sender.getSentRequests().contains(user)) return ResponseEntity.ok(false);

        Set<User> sentRequests = sender.getSentRequests();
        sentRequests.remove(user);
        sender.setSentRequests(sentRequests);

        Set<User> friendsOfSender = sender.getFriends();
        friendsOfSender.add(user);
        sender.setFriends(friendsOfSender);

        Set<User> friendsOfUser = user.getFriends();
        friendsOfUser.add(sender);
        user.setFriends(friendsOfUser);

        userRepository.save(user);
        userRepository.save(sender);

        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getFriends(HttpServletRequest request) {
        User user = getCurrentUser(request);
		if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(user.getFriends().stream().map(UserResponseDto::fromUser).toList());
    }

    @Override
    @Transactional
    public ResponseEntity<Boolean> unfriend(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
		if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User sender = userRepository.findById(id);
        Set<User> friendsOfSender = sender.getFriends();
        friendsOfSender.remove(user);
        sender.setFriends(friendsOfSender);
        Set<User> friendsOfUser = user.getFriends();
        friendsOfUser.remove(sender);
        user.setFriends(friendsOfUser);
        userRepository.save(user);
        userRepository.save(sender);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<Boolean> cancelFriendRequest(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
		if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User sender = userRepository.findById(id);
        Set<User> sentRequests = user.getSentRequests();
        sentRequests.remove(sender);
        user.setSentRequests(sentRequests);
        userRepository.save(user);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<Boolean> cancelReceivedRequest(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
		if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User sender = userRepository.findById(id);
        Set<User> sentRequests = sender.getSentRequests();
        sentRequests.remove(user);
        sender.setSentRequests(sentRequests);
        userRepository.save(sender);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getFriendsSuggestion(HttpServletRequest request) {

        User thisUser = getCurrentUser(request);
		if (thisUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<User> users = getAllUsers();
        users.remove(thisUser);
        users.removeAll(thisUser.getSentRequests());
        users.removeAll(getReceivedRequestsById(thisUser.getId())); // TODO: need to move this logic to UserRepository
        users.removeAll(thisUser.getFriends());
        users.sort((a,b)->Long.compare(b.getId(),a.getId()));
        return ResponseEntity.ok(users.stream().map(UserResponseDto::fromUser).toList());
    }
}
