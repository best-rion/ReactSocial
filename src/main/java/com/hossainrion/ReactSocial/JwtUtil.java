package com.hossainrion.ReactSocial;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public  class JwtUtil {
    private static final SecretKey secretKey = Keys.hmacShaKeyFor("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad".getBytes(StandardCharsets.UTF_8));
    public static final long JWT_TOKEN_EXPIRATION_TIME_IN_SECONDS = 5 * 60 ;
    public static final long REFRESH_TOKEN_EXPIRATION_TIME_IN_SECONDS = 60 * 60;


    public static String generateJwtToken(String username) {
        return generateToken(username, JWT_TOKEN_EXPIRATION_TIME_IN_SECONDS);
    }

    public static String generateRefreshToken(String username) {
        return generateToken(username, REFRESH_TOKEN_EXPIRATION_TIME_IN_SECONDS);
    }

    private static String generateToken(String username, long expiration_in_seconds) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration_in_seconds * 1000))
                .signWith(secretKey)
                .compact();
    }

    public static String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Optional: check expiration explicitly
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());

        } catch (ExpiredJwtException e) {
            // Token expired
            return false;
        } catch (SignatureException e) {
            // Invalid signature
            return false;
        } catch (Exception e) {
            // Any other parsing error
            return false;
        }
    }

    public static String getUsernameFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return getUsernameFromToken(header.split("Bearer ")[1]);
        }
        return null;
    }
}
