package com.hossainrion.ReactSocial.messaging.service;

import com.hossainrion.ReactSocial.messaging.dto.MessageProfileDto;
import com.hossainrion.ReactSocial.messaging.dto.MessageToSendDto;
import com.hossainrion.ReactSocial.messaging.entity.Message;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MessageService {
    Message saveMessage(String from, String to, String message);
    ResponseEntity<List<MessageToSendDto>> getMessages(String from, HttpServletRequest request);
    void setSeen(Long friendId, HttpServletRequest request);
    Message getById(Long messageId);
    ResponseEntity<List<MessageProfileDto>> getAll(HttpServletRequest request);
}
