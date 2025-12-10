package com.hossainrion.ReactSocial.messaging.dto;

import java.util.Date;

public record MessageProfileDto(String username, Boolean isSender, String fullName, String pictureBase64, String lastMessage, Date lastMessageTime, Integer unseenCount) {
}
