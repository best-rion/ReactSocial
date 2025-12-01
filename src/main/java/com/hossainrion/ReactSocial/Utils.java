package com.hossainrion.ReactSocial;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class Utils {
    public static void testDelay() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String generateToken(UserDetails userDetails) {
        // TODO: Change secret key and move it to application.properties
        Key key = Keys.hmacShaKeyFor("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad".getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60*60*1000))
                .signWith(key)
                .compact();
    }

}
