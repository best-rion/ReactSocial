package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.JwtUtil;
import com.hossainrion.ReactSocial.dto.PostAuthorDto;
import com.hossainrion.ReactSocial.dto.PostResponseDto;
import com.hossainrion.ReactSocial.dto.PostResponseForProfile;
import com.hossainrion.ReactSocial.dto.PostSaveDto;
import com.hossainrion.ReactSocial.entity.Post;
import com.hossainrion.ReactSocial.entity.PostLike;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.entity.composityKey.PostLikeId;
import com.hossainrion.ReactSocial.repository.PostLikeRepository;
import com.hossainrion.ReactSocial.repository.PostRepository;
import com.hossainrion.ReactSocial.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    PostServiceImpl(PostRepository postRepository, UserRepository userRepository, PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
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
    public List<PostResponseForProfile> getPosts(HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        List<Post> posts = postRepository.findAllByAuthorId(user.getId());
        return posts.stream().map(post -> new PostResponseForProfile(
                post.getId(),
                post.getContent(),
                post.getCreatedAt(),
                post.getMediaFileName(),
                post.getNumberOfLikes(),
                post.getNumberOfComments(),
                postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())
        )).toList();
    }

    @Override
    public List<PostResponseForProfile> getPostsByAuthorId(Long authorId, HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        List<Post> posts = postRepository.findAllByAuthorId(authorId);
        return posts.stream().map(post -> new PostResponseForProfile(
                post.getId(),
                post.getContent(),
                post.getCreatedAt(),
                post.getMediaFileName(),
                post.getNumberOfLikes(),
                post.getNumberOfComments(),
                postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())
        )).toList();
    }

    @Override
    public List<PostResponseDto> getAllFromFriends(HttpServletRequest request) {
        String email = JwtUtil.getEmailFromRequest(request);
        User user = userRepository.findByEmail(email);
        Set<User> friends = user.getFriends();
        return friends.stream().flatMap(friend -> postRepository.findAllByAuthorId(friend.getId()).stream())
                .map(post ->
                    new PostResponseDto(
                            post.getId(),
                            post.getContent(),
                            new PostAuthorDto(post.getAuthor().getId(), post.getAuthor().getFullName(), post.getAuthor().getPictureBase64()),
                            post.getMediaFileName(),
                            post.getCreatedAt(),
                            post.getNumberOfLikes(),
                            post.getNumberOfComments(),
                            postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())
                    )
                ).toList();
    }

    @Override
    @Transactional
    public Boolean like(Long postId, HttpServletRequest request) {
        User user = userRepository.findByEmail(JwtUtil.getEmailFromRequest(request));
        Post post = postRepository.findById(postId);

        boolean likeStatus = postLikeRepository.existsByUserIdAndPostId(user.getId(), postId);
        if (likeStatus) {
            post.setNumberOfLikes(post.getNumberOfLikes() - 1);

            postLikeRepository.deleteById(new PostLikeId(user.getId(), postId));
        } else {
            post.setNumberOfLikes(post.getNumberOfLikes() + 1);

            postLikeRepository.save(new PostLike(user, post));

            postRepository.save(post);
        }
        return ! likeStatus;
    }
}
