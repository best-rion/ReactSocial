package com.hossainrion.ReactSocial.dto;

import java.util.Date;

public record PostResponseForProfile(Long id, String content, Date createdAt, String mediaFileName, Long numberOfLikes, Long numberOfComments, Boolean liked) {
}
