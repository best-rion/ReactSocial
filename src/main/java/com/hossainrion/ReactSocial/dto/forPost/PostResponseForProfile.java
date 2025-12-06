package com.hossainrion.ReactSocial.dto.forPost;

import com.hossainrion.ReactSocial.entity.Post;

import java.util.Date;

public record PostResponseForProfile(Long id, String content, Date createdAt, String mediaFileName, Long numberOfLikes, Long numberOfComments, Boolean liked) {
    public static PostResponseForProfile fromPost(Post post, Boolean liked) {
        return new PostResponseForProfile(post.getId(), post.getContent(), post.getCreatedAt(), post.getMediaFileName(), post.getNumberOfLikes(), post.getNumberOfComments(), liked);
    }
}
