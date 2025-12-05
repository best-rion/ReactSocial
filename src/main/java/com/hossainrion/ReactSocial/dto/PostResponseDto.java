package com.hossainrion.ReactSocial.dto;

import java.util.Date;

public record PostResponseDto(Long id, String content, PostAuthorDto author, String mediaFileName, Date createdAt, Long numberOfLikes, Long numberOfComments, Boolean liked) {
}
