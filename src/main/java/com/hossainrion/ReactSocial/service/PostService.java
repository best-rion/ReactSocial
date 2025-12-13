package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.dto.forPost.PostResponseDto;
import com.hossainrion.ReactSocial.dto.forPost.PostResponseForProfile;
import com.hossainrion.ReactSocial.dto.forPost.PostSaveDto;
import com.hossainrion.ReactSocial.dto.forPost.PostUpdateDto;
import com.hossainrion.ReactSocial.entity.Post;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    ResponseEntity<Boolean> addPost(PostSaveDto postSaveDto, HttpServletRequest request);
    ResponseEntity<List<PostResponseForProfile>> getPosts(HttpServletRequest request);
    ResponseEntity<List<PostResponseForProfile>> getPostsByAuthorId(Long authorId, HttpServletRequest request);
    ResponseEntity<String> saveMedia(MultipartFile file);
    ResponseEntity<String> updateMedia(MultipartFile file, Long postId);
    ResponseEntity<List<PostResponseDto>> getAllFromFriends(HttpServletRequest request);
    ResponseEntity<Boolean> like(Long postId, HttpServletRequest request);
    ResponseEntity<Boolean> updatePost(PostUpdateDto postUpdateDto, HttpServletRequest request);
    ResponseEntity<Boolean> deletePostById(Long postId, HttpServletRequest request);
    Post getPostById(Long postId);
    void incrementCommentCount(Post post);
}
