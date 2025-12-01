package com.hossainrion.ReactSocial.dto;

import com.hossainrion.ReactSocial.entity.User;

public record UserResponseDto(String fullName, String email, String bio, String picture) {
    public static UserResponseDto fromUser(User user) {
        return new UserResponseDto(user.getFullName(), user.getEmail(), user.getBio(), user.getPicture());
    }
}
