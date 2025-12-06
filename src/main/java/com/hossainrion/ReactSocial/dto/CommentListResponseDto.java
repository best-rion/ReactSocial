package com.hossainrion.ReactSocial.dto;

import java.util.Date;

public record CommentListResponseDto(Long id, String content, PostAuthorDto author, Date createdAt) {
}
