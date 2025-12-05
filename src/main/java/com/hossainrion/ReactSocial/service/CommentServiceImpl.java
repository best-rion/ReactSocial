package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.dto.CommentSaveDto;
import com.hossainrion.ReactSocial.entity.Comment;
import com.hossainrion.ReactSocial.entity.Post;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.repository.CommentRepository;
import com.hossainrion.ReactSocial.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Boolean saveComment(CommentSaveDto commentSaveDto, HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User author = userRepository.findByEmail(email);
        Comment comment = new Comment();
        comment.setAuthor(author);
        Post post = new Post();
        post.setId(commentSaveDto.postId());
        comment.setPost(post);
        comment.setContent(commentSaveDto.content());
        comment.setCreatedAt(new Date());
        commentRepository.save(comment);
        return true;
    }
}
