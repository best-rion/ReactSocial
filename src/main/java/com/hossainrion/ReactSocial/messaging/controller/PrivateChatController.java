package com.hossainrion.ReactSocial.messaging.controller;

import com.hossainrion.ReactSocial.service.UserService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Objects;

@Controller
public class PrivateChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    public PrivateChatController(SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @MessageMapping("/private-message")
    public void sendPrivateMessage(Message<?> message) {

        messagingTemplate.convertAndSendToUser(
                "hossainrin",       // target username
                "/queue/private",       // private inbox
                message                 // message object
        );
    }
}
