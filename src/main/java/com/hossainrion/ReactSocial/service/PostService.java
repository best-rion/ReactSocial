package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.dto.PostResponseDto;
import com.hossainrion.ReactSocial.dto.PostResponseForProfile;
import com.hossainrion.ReactSocial.dto.PostSaveDto;
import com.hossainrion.ReactSocial.dto.PostUpdateDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    Boolean addPost(PostSaveDto postSaveDto, HttpServletRequest request);
    List<PostResponseForProfile> getPosts(HttpServletRequest request);
    List<PostResponseForProfile> getPostsByAuthorId(Long authorId, HttpServletRequest request);
    ResponseEntity<String> saveMedia(MultipartFile file);
    ResponseEntity<String> updateMedia(MultipartFile file, Long postId);
    List<PostResponseDto> getAllFromFriends(HttpServletRequest request);
    Boolean like(Long postId, HttpServletRequest request);
    Boolean updatePost(PostUpdateDto postUpdateDto, HttpServletRequest request);
}
