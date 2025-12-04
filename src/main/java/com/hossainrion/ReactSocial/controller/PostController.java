package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.dto.PostSaveDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/post")
public class PostController {
    @PostMapping
    public ResponseEntity<Boolean> createPost(@RequestBody PostSaveDto postSaveDto, HttpServletRequest request) {
        System.out.println(postSaveDto.content());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String uploadDir = "/home/hossain/Desktop/"; // create this folder in project root
        try {
            File dest = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(dest);
            return ResponseEntity.ok("File uploaded: " + dest.getName());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed");
        }
    }
}
