package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.utils.JwtUtil;
import com.hossainrion.ReactSocial.dto.JwtResponse;
import com.hossainrion.ReactSocial.dto.forUser.LoginDto;
import com.hossainrion.ReactSocial.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService) {this.userService = userService;}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        return userService.handleAuthentication(loginDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty() || !JwtUtil.isValid(refreshToken)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String username = JwtUtil.getUsernameFromToken(refreshToken);
        if (username == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();



        final String newToken = JwtUtil.generateJwtToken(username);
        final String newRefreshToken = JwtUtil.generateRefreshToken(username);
        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)       // must be false for HTTP localhost
                .sameSite("Lax")     // "None" requires secure=true
                .path("/")
                .maxAge(JwtUtil.REFRESH_TOKEN_EXPIRATION_TIME_IN_SECONDS)
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new JwtResponse( newToken, JwtUtil.JWT_TOKEN_EXPIRATION_TIME_IN_SECONDS * 1000));
    }

}
