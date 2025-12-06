package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.dto.forComment.CommentListResponseDto;
import com.hossainrion.ReactSocial.dto.forComment.CommentResponseDto;
import com.hossainrion.ReactSocial.dto.forComment.CommentSaveDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CommentService {
    CommentResponseDto saveComment(CommentSaveDto commentSaveDto, HttpServletRequest request);
    List<CommentListResponseDto> getAllByPostId(Long postId);
}
