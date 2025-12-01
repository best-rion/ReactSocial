package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.dto.LoginDto;
import com.hossainrion.ReactSocial.dto.UserSaveDto;
import com.hossainrion.ReactSocial.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    Boolean addUser(UserSaveDto userSaveDto);
    ResponseEntity<?> handleAuthentication(LoginDto loginDto);
}
