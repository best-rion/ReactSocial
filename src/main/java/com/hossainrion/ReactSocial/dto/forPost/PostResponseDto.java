package com.hossainrion.ReactSocial.dto.forPost;

import com.hossainrion.ReactSocial.dto.forUser.AuthorDto;
import com.hossainrion.ReactSocial.entity.Post;

import java.util.Date;

public record PostResponseDto(Long id, String content, AuthorDto author, String mediaFileName, Date createdAt, Long numberOfLikes, Long numberOfComments, Boolean liked) {
    public static PostResponseDto fromPost(Post post, Boolean liked) {
        return new PostResponseDto(post.getId(), post.getContent(), AuthorDto.fromUser(post.getAuthor()), post.getMediaFileName(), post.getCreatedAt(), post.getNumberOfLikes(), post.getNumberOfComments(), liked);
    }
}
