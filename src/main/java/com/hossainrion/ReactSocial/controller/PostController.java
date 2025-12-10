package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.dto.*;
import com.hossainrion.ReactSocial.dto.forPost.PostResponseDto;
import com.hossainrion.ReactSocial.dto.forPost.PostResponseForProfile;
import com.hossainrion.ReactSocial.dto.forPost.PostSaveDto;
import com.hossainrion.ReactSocial.dto.forPost.PostUpdateDto;
import com.hossainrion.ReactSocial.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    PostController(PostService postService) {this.postService = postService;}

    @PostMapping
    public ResponseEntity<Boolean> createPost(@RequestBody PostSaveDto postSaveDto, HttpServletRequest request) {
        return postService.addPost(postSaveDto, request);
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean> updatePost(@RequestBody PostUpdateDto postUpdateDto, HttpServletRequest request) {
        return postService.updatePost(postUpdateDto, request);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return postService.saveMedia(file);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Boolean> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        return postService.deletePostById(postId, request);
    }

    @PostMapping("/upload/update")
    public ResponseEntity<String> uploadUpdateFile(@RequestParam("file") MultipartFile file, @RequestParam("postId") Long postId) {
        return postService.updateMedia(file, postId);
    }

    @GetMapping("/get-for-profile")
    public ResponseEntity<List<PostResponseForProfile>> getPostForProfile(HttpServletRequest request) {
        return postService.getPosts(request);
    }

    @GetMapping("/get-for-profile/{profileId}")
    public ResponseEntity<List<PostResponseForProfile>> getPostForProfile(@PathVariable("profileId") Long profileId, HttpServletRequest request) {
        return postService.getPostsByAuthorId(profileId, request);
    }

    @GetMapping("/get-for-homepage")
    public ResponseEntity<List<PostResponseDto>> getPostForHomepage(HttpServletRequest request) {
        return postService.getAllFromFriends(request);
    }

    @PostMapping("/like")
    public ResponseEntity<Boolean> likePost(@RequestBody IdDto postIdDto, HttpServletRequest request) {
        return postService.like(postIdDto.id(), request);
    }
}
