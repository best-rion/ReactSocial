package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.dto.LoginDto;
import com.hossainrion.ReactSocial.dto.UserSaveDto;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {this.userService = userService;}

    @PostMapping("/save")
    public ResponseEntity<Boolean> addUser(@RequestBody UserSaveDto userSaveDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(userSaveDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream().map(User::getUsername).toList());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        return userService.handleAuthentication(loginDto);
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(HttpServletRequest request) {
        String email = JwtUtil.getEmailFromToken(request.getHeader("Authorization").split("Bearer ")[1]);
        return ResponseEntity.ok(userService.getUserByEmail(email).getFullName());
    }
}
