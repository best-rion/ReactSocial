package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.dto.PostResponseDto;
import com.hossainrion.ReactSocial.dto.PostResponseForProfile;
import com.hossainrion.ReactSocial.dto.PostSaveDto;
import com.hossainrion.ReactSocial.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    PostController(PostService postService) {this.postService = postService;}

    @PostMapping
    public ResponseEntity<Boolean> createPost(@RequestBody PostSaveDto postSaveDto, HttpServletRequest request) {
        return ResponseEntity.ok(postService.addPost(postSaveDto.content(), request));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String uploadDir = "/home/hossain/Desktop/"; // create this folder in project root
        try {
            File dest = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(dest);
            return ResponseEntity.ok(dest.getName());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed");
        }
    }

    @GetMapping("/get-for-profile")
    public ResponseEntity<List<PostResponseForProfile>> getPostForProfile(HttpServletRequest request) {
        return ResponseEntity.ok(postService.getPosts(request));
    }
}
