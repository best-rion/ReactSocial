package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.dto.PostResponseForProfile;
import com.hossainrion.ReactSocial.entity.Post;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.repository.PostRepository;
import com.hossainrion.ReactSocial.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Boolean addPost(String content, HttpServletRequest httpServletRequest) {
        String email = JwtUtil.getEmailFromRequest(httpServletRequest);
        User user = userRepository.findByEmail(email);
        Post post = new Post();
        post.setContent(content);
        post.setAuthor(user);
        post.setCreatedAt(new Date());
        postRepository.save(post);
        return true;
    }

    @Override
    public List<PostResponseForProfile> getPosts(HttpServletRequest httpServletRequest) {
        String email = JwtUtil.getEmailFromRequest(httpServletRequest);
        User user = userRepository.findByEmail(email);
        List<Post> posts = postRepository.findAllByAuthorId(user.getId());
        return posts.stream().map(post -> new PostResponseForProfile(post.getId(),post.getContent(), post.getCreatedAt())).toList();
    }
}
