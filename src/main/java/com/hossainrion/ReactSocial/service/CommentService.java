package com.hossainrion.ReactSocial.service;

import com.hossainrion.ReactSocial.dto.CommentSaveDto;
import jakarta.servlet.http.HttpServletRequest;

public interface CommentService {
    Boolean saveComment(CommentSaveDto commentSaveDto, HttpServletRequest request);
}
