package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.Util;
import com.hossainrion.ReactSocial.dto.*;
import com.hossainrion.ReactSocial.dto.forUser.LoginDto;
import com.hossainrion.ReactSocial.dto.forUser.UserResponseDto;
import com.hossainrion.ReactSocial.dto.forUser.UserSaveDto;
import com.hossainrion.ReactSocial.dto.forUser.UserUpdateDto;
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
        return userRepository.findByUsername(JwtUtil.getusernameFromRequest(request));
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
        user.setUsername(userSaveDto.username());
        user.setPassword(userSaveDto.password());
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateUser(UserUpdateDto userUpdateDto, HttpServletRequest request) {
        User user = getCurrentUser(request);
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
        return true;
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
        final String jwt = JwtUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return UserResponseDto.fromUser(userRepository.findById(id));
    }

    @Override
    public UserResponseDto getUser(HttpServletRequest request) {
        return UserResponseDto.fromUser(getCurrentUser(request));
    }

    @Override
    public List<UserResponseDto> getSentRequests(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user.getSentRequests().stream().map(UserResponseDto::fromUser).toList();
    }

    @Override
    public List<UserResponseDto> getReceivedRequests(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return getReceivedRequestsById(user.getId()).stream().map(UserResponseDto::fromUser).toList();
    }

    @Override
    public List<User> getReceivedRequestsById(Long id) {
        return userRepository.findReceivedRequestsById(id);
    }

    @Override
    public Boolean addFriend(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
        Set<User> sentRequests = user.getSentRequests();
        sentRequests.add(User.builder().id(id).build());
        user.setSentRequests(sentRequests);
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public Boolean acceptFriendRequest(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
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
        User user = getCurrentUser(request);
        return user.getFriends().stream().map(UserResponseDto::fromUser).toList();
    }

    @Override
    @Transactional
    public Boolean unfriend(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
        User sender = userRepository.findById(id);
        Set<User> friendsOfSender = sender.getFriends();
        friendsOfSender.remove(user);
        sender.setFriends(friendsOfSender);
        Set<User> friendsOfUser = user.getFriends();
        friendsOfUser.remove(sender);
        user.setFriends(friendsOfUser);
        userRepository.save(user);
        userRepository.save(sender);
        return true;
    }

    @Override
    public Boolean cancelFriendRequest(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
        User sender = userRepository.findById(id);
        Set<User> sentRequests = user.getSentRequests();
        sentRequests.remove(sender);
        user.setSentRequests(sentRequests);
        userRepository.save(user);
        return true;
    }

    @Override
    public Boolean cancelReceivedRequest(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
        User sender = userRepository.findById(id);
        Set<User> sentRequests = sender.getSentRequests();
        sentRequests.remove(user);
        sender.setSentRequests(sentRequests);
        userRepository.save(sender);
        return true;
    }

    @Override
    public List<UserResponseDto> getFriendsSuggestion(HttpServletRequest request) {
        List<User> users = getAllUsers();
        User thisUser = getCurrentUser(request);
        users.remove(thisUser);
        users.removeAll(thisUser.getSentRequests());
        users.removeAll(getReceivedRequestsById(thisUser.getId())); // TODO: need to move this logic to UserRepository
        users.removeAll(thisUser.getFriends());
        users.sort((a,b)->Long.compare(b.getId(),a.getId()));
        return users.stream().map(UserResponseDto::fromUser).toList();
    }
}
