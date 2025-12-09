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
    public Boolean addPost(PostSaveDto postSaveDto, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        Post post = Post.builder().
                content(postSaveDto.content()).
                mediaFileName(postSaveDto.fileName()).
                author(user).
                numberOfLikes((long) 0).
                numberOfComments((long) 0).
                createdAt(new Date()).build();
        postRepository.save(post);
        return true;
    }

    @Override
    public Boolean updatePost(PostUpdateDto postUpdateDto, HttpServletRequest request) {
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
        return true;
    }

    @Override
    @Transactional
    public Boolean deletePostById(Long postId, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        Post post = postRepository.findById(postId);
        if (post.getAuthor().getId() != user.getId()) return false;
        if (! post.getMediaFileName().isEmpty()) {
            Util.deleteMedia(post.getMediaFileName());
        }
        postLikeRepository.deleteAllByPost(post);
        commentRepository.deleteAllByPost(post);
        postRepository.delete(post);
        return true;
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
    public List<PostResponseForProfile> getPosts(HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        List<Post> posts = postRepository.findAllByAuthorId(user.getId());
        return posts.stream().map(post ->
                PostResponseForProfile.fromPost(
                        post,
                        postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())
                )
        ).toList();
    }

    @Override
    public List<PostResponseForProfile> getPostsByAuthorId(Long authorId, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        List<Post> posts = postRepository.findAllByAuthorId(authorId);
        return posts.stream().map(post ->
                PostResponseForProfile.fromPost(
                        post,
                        postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())
        )).toList();
    }

    @Override
    public List<PostResponseDto> getAllFromFriends(HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        Set<User> friends = user.getFriends();
        return friends.stream().flatMap(friend -> postRepository.findAllByAuthorId(friend.getId()).stream())
                .map(post ->
                    PostResponseDto.fromPost(
                            post,
                            postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())
                    )
                ).toList();
    }

    @Override
    @Transactional
    public Boolean like(Long postId, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        Post post = postRepository.findById(postId);

        boolean likeStatus = postLikeRepository.existsByUserIdAndPostId(user.getId(), postId);
        if (likeStatus) {
            post.setNumberOfLikes(post.getNumberOfLikes() - 1);
            postLikeRepository.deleteById(new PostLikeId(user.getId(), postId));
        } else {
            post.setNumberOfLikes(post.getNumberOfLikes() + 1);
            postLikeRepository.save(new PostLike(user, post));
        }
        postRepository.save(post);
        return !likeStatus;
    }
}
