package com.hossainrion.ReactSocial;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public  class JwtUtil {
    private static final SecretKey secretKey = Keys.hmacShaKeyFor("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad".getBytes(StandardCharsets.UTF_8));

    public static String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60*60*1000))
                .signWith(secretKey)
                .compact();
    }

    public static String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
