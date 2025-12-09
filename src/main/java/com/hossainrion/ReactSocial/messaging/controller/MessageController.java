package com.hossainrion.ReactSocial.messaging.controller;

import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.messaging.dto.MessageToSendDto;
import com.hossainrion.ReactSocial.messaging.entity.Message;
import com.hossainrion.ReactSocial.messaging.service.MessageService;
import com.hossainrion.ReactSocial.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;
    MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{friendName}")
    public ResponseEntity<List<MessageToSendDto>> getMessage(@PathVariable("friendName") String friendName, HttpServletRequest request) {
        return ResponseEntity.ok(messageService.getMessages(friendName, request));
    }
}
