package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.dto.CommentListResponseDto;
import com.hossainrion.ReactSocial.dto.CommentResponseDto;
import com.hossainrion.ReactSocial.dto.CommentSaveDto;
import com.hossainrion.ReactSocial.dto.PostAuthorDto;
import com.hossainrion.ReactSocial.entity.Comment;
import com.hossainrion.ReactSocial.entity.Post;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.repository.CommentRepository;
import com.hossainrion.ReactSocial.repository.PostRepository;
import com.hossainrion.ReactSocial.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public CommentResponseDto saveComment(CommentSaveDto commentSaveDto, HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User author = userRepository.findByEmail(email);
        Comment comment = new Comment();
        comment.setAuthor(author);
        Post post = postRepository.findById(commentSaveDto.postId());
        comment.setPost(post);
        comment.setContent(commentSaveDto.content());
        comment.setCreatedAt(new Date());
        comment = commentRepository.save(comment);
        post.setNumberOfComments(post.getNumberOfComments() + 1);
        postRepository.save(post);
        return new CommentResponseDto(comment.getId(), comment.getCreatedAt());
    }

    @Override
    public List<CommentListResponseDto> getAllByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId).stream().map(
                comment -> new CommentListResponseDto(
                        comment.getId(),
                        comment.getContent(),
                        new PostAuthorDto(comment.getAuthor().getId(), comment.getAuthor().getFullName(), comment.getAuthor().getPictureBase64()),
                        comment.getCreatedAt()
                )
        ).toList();
    }
}
