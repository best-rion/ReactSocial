package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.utils.Util;
import com.hossainrion.ReactSocial.dto.forPost.PostResponseDto;
import com.hossainrion.ReactSocial.dto.forPost.PostResponseForProfile;
import com.hossainrion.ReactSocial.dto.forPost.PostSaveDto;
import com.hossainrion.ReactSocial.dto.forPost.PostUpdateDto;
import com.hossainrion.ReactSocial.entity.Post;
import com.hossainrion.ReactSocial.entity.PostLike;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.entity.composityKey.PostLikeId;
import com.hossainrion.ReactSocial.repository.CommentRepository;
import com.hossainrion.ReactSocial.repository.PostLikeRepository;
import com.hossainrion.ReactSocial.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    PostServiceImpl(PostRepository postRepository, UserService userService, PostLikeRepository postLikeRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ResponseEntity<String> saveMedia(MultipartFile file) {
        return Util.saveMediaPhoto(file);
    }

    @Override
    public ResponseEntity<String> updateMedia(MultipartFile file, Long postId) {
        Post post = postRepository.findById(postId);
        Util.deleteMedia(post.getMediaFileName());
        return saveMedia(file);
    }

    @Override
    public ResponseEntity<Boolean> addPost(PostSaveDto postSaveDto, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Post post = Post.builder().
                content(postSaveDto.content()).
                mediaFileName(postSaveDto.fileName()).
                author(user).
                numberOfLikes((long) 0).
                numberOfComments((long) 0).
                createdAt(new Date()).build();
        postRepository.save(post);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<Boolean> updatePost(PostUpdateDto postUpdateDto, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Post post = postRepository.findById(postUpdateDto.id());
        post.setContent(postUpdateDto.content());
        if (! postUpdateDto.fileName().isEmpty())
        {
            post.setMediaFileName(postUpdateDto.fileName());
        } else {
            if (postUpdateDto.removeMedia()) {
                Util.deleteMedia(post.getMediaFileName());
                post.setMediaFileName("");
            }
        }
        postRepository.save(post);
        return ResponseEntity.ok(true);
    }

    @Override
    @Transactional
    public ResponseEntity<Boolean> deletePostById(Long postId, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Post post = postRepository.findById(postId);
        if (post == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if (post.getAuthor().getId() != user.getId()) return ResponseEntity.ok(false);

        if (! post.getMediaFileName().isEmpty()) {
            Util.deleteMedia(post.getMediaFileName());
        }

        postLikeRepository.deleteAllByPost(post);
        commentRepository.deleteAllByPost(post);
        postRepository.delete(post);
        return ResponseEntity.ok(true);
    }

    @Override
    public Post getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    public void incrementCommentCount(Post post) {
        post.setNumberOfComments(post.getNumberOfComments() + 1);
        postRepository.save(post);
    }

    @Override
    public ResponseEntity<List<PostResponseForProfile>> getPosts(HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<PostResponseForProfile> posts = postRepository.findAllByAuthorId(user.getId()).stream().map(post ->
                PostResponseForProfile.fromPost(
                        post,
                        postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())
                )
        ).toList();
        return ResponseEntity.ok(posts);
    }

    @Override
    public ResponseEntity<List<PostResponseForProfile>> getPostsByAuthorId(Long authorId, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<PostResponseForProfile> posts = postRepository.findAllByAuthorId(authorId).stream().map(post ->
                PostResponseForProfile.fromPost(
                        post,
                        postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())
        )).toList();
        return ResponseEntity.ok(posts);
    }

    @Override
    public ResponseEntity<List<PostResponseDto>> getAllFromFriends(HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Set<User> friends = user.getFriends();
        List<PostResponseDto> posts = friends.stream().flatMap(friend -> postRepository.findAllByAuthorId(friend.getId()).stream())
                .map(post ->
                    PostResponseDto.fromPost(
                            post,
                            postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())
                    )
                ).toList();
        return ResponseEntity.ok(posts);
    }

    @Override
    @Transactional
    public ResponseEntity<Boolean> like(Long postId, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Post post = postRepository.findById(postId);
        if (post == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        boolean likeStatus = postLikeRepository.existsByUserIdAndPostId(user.getId(), postId);
        if (likeStatus) {
            post.setNumberOfLikes(post.getNumberOfLikes() - 1);
            postLikeRepository.deleteById(new PostLikeId(user.getId(), postId));
        } else {
            post.setNumberOfLikes(post.getNumberOfLikes() + 1);
            postLikeRepository.save(new PostLike(user, post));
        }
        postRepository.save(post);
        return ResponseEntity.ok(!likeStatus);
    }
}
