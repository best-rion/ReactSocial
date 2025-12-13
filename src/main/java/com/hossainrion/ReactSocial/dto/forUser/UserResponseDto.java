package com.hossainrion.ReactSocial.dto.forUser;

import com.hossainrion.ReactSocial.entity.User;

public record UserResponseDto(Long id, String fullName, String username, String bio, String pictureBase64, FriendStatus friendStatus) {
    public static UserResponseDto fromUser(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getBio(),
                user.getPictureBase64(),
                null
        );
    }

    public static UserResponseDto fromUserWithFriendStatus(User user, FriendStatus friendStatus) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getBio(),
                user.getPictureBase64(),
                friendStatus
        );
    }

}
