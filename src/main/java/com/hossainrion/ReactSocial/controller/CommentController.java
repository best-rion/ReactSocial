package com.hossainrion.ReactSocial.controller;

import com.hossainrion.ReactSocial.dto.forComment.CommentListResponseDto;
import com.hossainrion.ReactSocial.dto.forComment.CommentResponseDto;
import com.hossainrion.ReactSocial.dto.forComment.CommentSaveDto;
import com.hossainrion.ReactSocial.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;
    CommentController(CommentService commentService) {this.commentService = commentService;}

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentSaveDto commentSaveDto, HttpServletRequest request) {
        return commentService.saveComment(commentSaveDto, request);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentListResponseDto>> getAllComments(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(commentService.getAllByPostId(postId));
    }
}
