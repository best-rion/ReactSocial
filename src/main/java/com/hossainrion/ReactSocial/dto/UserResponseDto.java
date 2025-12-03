package com.hossainrion.ReactSocial.dto;

import com.hossainrion.ReactSocial.entity.User;

public record UserResponseDto(Long id, String fullName, String email, String bio, String pictureBase64) {
    public static UserResponseDto fromUser(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getBio(),
                user.getPictureBase64()
        );
    }
}
