package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.Utils;
import com.hossainrion.ReactSocial.dto.JwtResponse;
import com.hossainrion.ReactSocial.dto.LoginDto;
import com.hossainrion.ReactSocial.dto.UserSaveDto;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.repository.UserRepository;
import com.hossainrion.ReactSocial.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
        Utils.testDelay();
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
}
