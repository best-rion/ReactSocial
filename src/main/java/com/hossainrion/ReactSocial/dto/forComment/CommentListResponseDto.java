package com.hossainrion.ReactSocial.dto.forComment;

import com.hossainrion.ReactSocial.dto.forUser.AuthorDto;
import com.hossainrion.ReactSocial.entity.Comment;

import java.util.Date;

public record CommentListResponseDto(Long id, String content, AuthorDto author, Date createdAt) {
    public static CommentListResponseDto fromComment(Comment comment) {
        return new CommentListResponseDto(comment.getId(), comment.getContent(), AuthorDto.fromUser(comment.getAuthor()), comment.getCreatedAt());
    }
}
