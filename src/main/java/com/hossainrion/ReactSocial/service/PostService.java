package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.dto.PostResponseDto;
import com.hossainrion.ReactSocial.dto.PostResponseForProfile;
import com.hossainrion.ReactSocial.dto.PostSaveDto;
import com.hossainrion.ReactSocial.entity.Post;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PostService {
    Boolean addPost(String content, HttpServletRequest request);
    List<PostResponseForProfile> getPosts(HttpServletRequest request);
}
