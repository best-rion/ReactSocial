package com.hossainrion.ReactSocial.dto;

import com.hossainrion.ReactSocial.entity.User;

public record UserResponseDto(String fullName, String email, String bio, String pictureBase64) { }
