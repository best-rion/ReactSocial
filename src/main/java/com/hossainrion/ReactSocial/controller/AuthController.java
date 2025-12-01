package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.dto.LoginDto;
import com.hossainrion.ReactSocial.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService) {this.userService = userService;}

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        return userService.handleAuthentication(loginDto);
    }
}
