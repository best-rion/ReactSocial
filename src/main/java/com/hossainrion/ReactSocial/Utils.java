package com.hossainrion.ReactSocial;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

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
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60*60*1000))
                .signWith(SignatureAlgorithm.HS256, "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad") // TODO: Change secret key and move it to application.properties
                .compact();
    }

}
