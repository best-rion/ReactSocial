package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.dto.UserResponseDto;
import com.hossainrion.ReactSocial.dto.UserSaveDto;
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
        String email = JwtUtil.getEmailFromToken(request.getHeader("Authorization").split("Bearer ")[1]);
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(UserResponseDto.fromUser(user));
    }

    @PostMapping
    public ResponseEntity<Boolean> saveUser(@RequestBody UserSaveDto userSaveDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(userSaveDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream().map(User::getUsername).toList());
    }


}
