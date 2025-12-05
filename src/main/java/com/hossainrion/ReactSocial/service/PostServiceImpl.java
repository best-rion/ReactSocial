package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.dto.PostAuthorDto;
import com.hossainrion.ReactSocial.dto.PostResponseDto;
import com.hossainrion.ReactSocial.dto.PostResponseForProfile;
import com.hossainrion.ReactSocial.dto.PostSaveDto;
import com.hossainrion.ReactSocial.entity.Post;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.repository.PostRepository;
import com.hossainrion.ReactSocial.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<String> saveMedia(MultipartFile file) {
        String uploadDir = "/home/hossain/Desktop/"; // create this folder in project root
        try {
            String type = file.getContentType();
            if (type != null && type.equals("image/png")) {
                String fileName = UUID.randomUUID() + ".png";
                File dest = new File(uploadDir + "photos/" + fileName);
                file.transferTo(dest);
                return ResponseEntity.ok(fileName);
            } else if (type != null && type.equals("video/mp4")) {
                String fileName = UUID.randomUUID() + ".mp4";
                File dest = new File(uploadDir + "videos/" + fileName);
                file.transferTo(dest);
                return ResponseEntity.ok(fileName);
            }
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed");
        }
    }

    @Override
    public Boolean addPost(PostSaveDto postSaveDto, HttpServletRequest httpServletRequest) {
        String email = JwtUtil.getEmailFromRequest(httpServletRequest);
        User user = userRepository.findByEmail(email);
        Post post = new Post();
        post.setContent(postSaveDto.content());
        post.setMediaFileName(postSaveDto.fileName());
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
        return posts.stream().map(post -> new PostResponseForProfile(post.getId(),post.getContent(), post.getCreatedAt(), post.getMediaFileName())).toList();
    }

    @Override
    public List<PostResponseDto> getAllFromFriends(HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        Set<User> friends = user.getFriends();
        return friends.stream().flatMap(friend -> postRepository.findAllByAuthorId(friend.getId()).stream())
                .map(post ->
                    new PostResponseDto(post.getId(), post.getContent(), new PostAuthorDto(post.getAuthor().getId(), post.getAuthor().getFullName(), post.getAuthor().getPictureBase64()), post.getMediaFileName(), post.getCreatedAt())
                ).toList();
    }
}
