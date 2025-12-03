package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.dto.LoginDto;
import com.hossainrion.ReactSocial.dto.UserResponseDto;
import com.hossainrion.ReactSocial.dto.UserSaveDto;
import com.hossainrion.ReactSocial.dto.UserUpdateDto;
import com.hossainrion.ReactSocial.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    UserResponseDto getUser(HttpServletRequest request);
    List<User> getAllUsers();
    Boolean addUser(UserSaveDto userSaveDto);
    Boolean updateUser(UserUpdateDto userUpdateDto, HttpServletRequest request);
    ResponseEntity<?> handleAuthentication(LoginDto loginDto);
    User getUserByEmail(String email);
}
