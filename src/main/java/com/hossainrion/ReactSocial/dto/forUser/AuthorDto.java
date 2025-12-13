package com.hossainrion.ReactSocial.dto.forUser;

import com.hossainrion.ReactSocial.entity.User;

public record AuthorDto(Long id, String name, String pictureBase64) {
    public static AuthorDto fromUser(User user) {
        return new AuthorDto(user.getId(), user.getFullName(), user.getPictureBase64());
    }
}
