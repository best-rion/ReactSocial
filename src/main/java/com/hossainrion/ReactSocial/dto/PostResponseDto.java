package com.hossainrion.ReactSocial.dto;

import java.util.Date;

public record PostResponseDto(Long id, String content, PostAuthorDto author, Date createdAt) {
}
