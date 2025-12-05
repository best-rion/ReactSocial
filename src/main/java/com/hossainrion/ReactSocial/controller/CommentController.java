package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.dto.CommentSaveDto;
import com.hossainrion.ReactSocial.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;
    CommentController(CommentService commentService) {this.commentService = commentService;}

    @PostMapping
    public ResponseEntity<Boolean> createComment(@RequestBody CommentSaveDto commentSaveDto, HttpServletRequest request) {
        return ResponseEntity.ok(commentService.saveComment(commentSaveDto, request));
    }
}
