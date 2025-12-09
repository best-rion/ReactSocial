package com.hossainrion.ReactSocial.messaging.controller;

import com.hossainrion.ReactSocial.dto.IdDto;
import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.messaging.dto.MessageToSendDto;
import com.hossainrion.ReactSocial.messaging.entity.Message;
import com.hossainrion.ReactSocial.messaging.service.MessageService;
import com.hossainrion.ReactSocial.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
    }

    @GetMapping("/{friendName}")
    public ResponseEntity<List<MessageToSendDto>> getMessage(@PathVariable("friendName") String friendName, HttpServletRequest request) {
        return ResponseEntity.ok(messageService.getMessages(friendName, request));
    }

    @PostMapping("/seen")
    public void seenMessages(@RequestBody IdDto friendId, HttpServletRequest request) {
        messageService.setSeen(friendId.id(), request);
    }
}
