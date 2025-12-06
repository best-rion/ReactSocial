package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.dto.forComment.CommentListResponseDto;
import com.hossainrion.ReactSocial.dto.forComment.CommentResponseDto;
import com.hossainrion.ReactSocial.dto.forComment.CommentSaveDto;
import com.hossainrion.ReactSocial.entity.Comment;
import com.hossainrion.ReactSocial.entity.Post;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.repository.CommentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    CommentServiceImpl(CommentRepository commentRepository, UserService userService, PostService postService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postService = postService;
    }

    @Override
    public CommentResponseDto saveComment(CommentSaveDto commentSaveDto, HttpServletRequest request) {
        User author = userService.getCurrentUser(request);
        Post post = postService.getPostById(commentSaveDto.postId());
        Comment comment = Comment.builder().
                author(author).
                post(post).
                content(commentSaveDto.content()).
                createdAt(new Date()).build();
        comment = commentRepository.save(comment);
        postService.incrementCommentCount(post);
        return new CommentResponseDto(comment.getId(), comment.getCreatedAt());
    }

    @Override
    public List<CommentListResponseDto> getAllByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId).stream().map(CommentListResponseDto::fromComment).toList();
    }
}
