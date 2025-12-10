package com.hossainrion.ReactSocial.dto;

public record JwtResponse(String token, long expiration_milliseconds) {
}
