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

import java.util.List;

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
    public UserResponseDto getUser(HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        return UserResponseDto.fromUser(user);
    }
}
