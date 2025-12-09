package com.hossainrion.ReactSocial.messaging.dto;

import com.hossainrion.ReactSocial.messaging.entity.Message;

public record MessageToSendDto(Long id, String sender, String text, String timestamp) {
    public static MessageToSendDto of(Message message) {
        return new MessageToSendDto(message.getId(), message.getSender().getUsername(), message.getContent(), message.getTime().toString());
    }
}
