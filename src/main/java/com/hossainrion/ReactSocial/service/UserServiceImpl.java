package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.Util;
import com.hossainrion.ReactSocial.dto.*;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Boolean addUser(UserSaveDto userSaveDto) {
        User user = new User();
        user.setFullName(userSaveDto.fullName());
        user.setEmail(userSaveDto.email());
        user.setPassword(userSaveDto.password());
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateUser(UserUpdateDto userUpdateDto, HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        user.setFullName(userUpdateDto.fullName());
        user.setBio(userUpdateDto.bio());

        try {
            if (user.getPicture() != null && Util.pictureExists(user.getPicture())) {
                Util.deleteFile(user.getPicture());
            }
            if (!userUpdateDto.pictureBase64().isEmpty()) {
                String fileName = Util.savePicture(userUpdateDto.pictureBase64());
                if (fileName != null) {
                    user.setPicture(fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        userRepository.save(user);
        return true;
    }

    @Override
    public ResponseEntity<?> handleAuthentication(LoginDto loginDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final UserDetails user = userDetailsService.loadUserByUsername(loginDto.email());
        final String jwt = JwtUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return UserResponseDto.fromUser(userRepository.findById(id));
    }

    @Override
    public UserResponseDto getUser(HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        return UserResponseDto.fromUser(user);
    }

    @Override
    public List<UserResponseDto> getSentRequests(HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        return new ArrayList<>(user.getSentRequests()).stream().map(UserResponseDto::fromUser).toList();
    }

    @Override
    public List<UserResponseDto> getReceivedRequests(HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        return getReceivedRequestsById(user.getId()).stream().map(UserResponseDto::fromUser).toList();
    }

    @Override
    public List<User> getReceivedRequestsById(Long id) {
        return userRepository.findReceivedRequestsById(id);
    }

    @Override
    public Boolean addFriend(Long id, HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        User newRequest = new User();
        newRequest.setId(id);
        Set<User> sentRequests = user.getSentRequests();
        sentRequests.add(newRequest);
        user.setSentRequests(sentRequests);
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public Boolean acceptFriendRequest(Long id, HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        User sender = userRepository.findById(id);
        if (!sender.getSentRequests().contains(user)) return false;

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

        return true;
    }

    @Override
    public List<UserResponseDto> getFriends(HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        return new ArrayList<>(user.getFriends()).stream().map(UserResponseDto::fromUser).toList();
    }

    @Override
    @Transactional
    public Boolean unfriend(Long id, HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        User sender = userRepository.findById(id);
        Set<User> friendsOfSender = sender.getFriends();
        friendsOfSender.remove(user);
        sender.setFriends(friendsOfSender);
        Set<User> friendsOfUser = user.getFriends();
        friendsOfUser.remove(sender);
        user.setFriends(friendsOfUser);
        userRepository.save(user);
        return true;
    }

    @Override
    public Boolean cancelFriendRequest(Long id, HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        User sender = userRepository.findById(id);
        Set<User> sentRequests = user.getSentRequests();
        sentRequests.remove(sender);
        user.setSentRequests(sentRequests);
        userRepository.save(user);
        return true;
    }

    @Override
    public Boolean cancelReceivedRequest(Long id, HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        User sender = userRepository.findById(id);
        Set<User> sentRequests = sender.getSentRequests();
        sentRequests.remove(user);
        sender.setSentRequests(sentRequests);
        userRepository.save(sender);
        return true;
    }
}
