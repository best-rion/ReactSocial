package com.hossainrion.ReactSocial.messaging.service;

import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.messaging.dto.MessageProfileDto;
import com.hossainrion.ReactSocial.messaging.dto.MessageToSendDto;
import com.hossainrion.ReactSocial.messaging.entity.Message;
import com.hossainrion.ReactSocial.messaging.repository.MessageRepository;
import com.hossainrion.ReactSocial.service.UserService;
import com.hossainrion.ReactSocial.utils.Util;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static com.hossainrion.ReactSocial.utils.Util.sessionManager;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    MessageServiceImpl(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @Override
    public Message saveMessage(String from, String to, String message) {
        Message msg = new Message();
        msg.setSender(userService.getUserByUsername(from));
        msg.setReceiver(userService.getUserByUsername(to));
        msg.setContent(message);
        msg.setTime(new Date());
        msg.setSeen(0);
        return messageRepository.save(msg);
    }

    @Override
    public ResponseEntity<List<MessageToSendDto>> getMessages(String from, HttpServletRequest request) {
        User thisUser = userService.getCurrentUser(request);
        if (thisUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User friend = userService.getUserByUsername(from);
        if (friend == null) return ResponseEntity.notFound().build();

        List<Message> friendAndMe = messageRepository.findAllBySenderAndReceiver(friend, thisUser);
        List<Message> meAndFriend = messageRepository.findAllBySenderAndReceiver(thisUser , friend);
        return ResponseEntity.ok(Stream.concat(friendAndMe.stream(), meAndFriend.stream()).sorted(Comparator.comparing(Message::getTime)).map(MessageToSendDto::of).toList());
    }

    @Override
    public ResponseEntity<Boolean> setSeen(Long friendId, HttpServletRequest request) {
        User thisUser = userService.getCurrentUser(request);
        if (thisUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User friend = userService.getUserById(friendId);
        if (friend == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        messageRepository.setSeenBySenderIdAndReceiverId(friendId, thisUser.getId());

        List<WebSocketSession> sessions = sessionManager.getSessions(friend.getUsername());
        if (sessions != null && !sessions.isEmpty()) {
            sessions.forEach(s -> {
                if (s == null || !s.isOpen()) {
                    sessions.remove(s);
                    return;
                }
                try {
                    String dedicatedTo = (String) s.getAttributes().get("dedicatedTo");
                    if (dedicatedTo.equals(thisUser.getUsername())) {
                        s.sendMessage(new TextMessage("seen"));
                    }
                    if (dedicatedTo.equals("MESSAGES")) {
                        s.sendMessage(new TextMessage(Util.toJsonString(new MessageProfileDto(
                                thisUser.getUsername(),
                                false,
                                null,
                                null,
                                null,
                                null,
                                0

                        ))));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            System.out.println("No sessions found for " + friend.getUsername());
        }


        List<WebSocketSession> sessionsMy = sessionManager.getSessions(thisUser.getUsername());
        if (sessionsMy != null && !sessionsMy.isEmpty()) {
            sessionsMy.forEach(s -> {
                if (s == null || !s.isOpen()) {
                    sessionManager.removeSession(s);
                    return;
                }
                try {
                    String dedicatedTo = (String) s.getAttributes().get("dedicatedTo");
                    if (dedicatedTo.equals("MESSAGES")) {
                        s.sendMessage(new TextMessage(Util.toJsonString(new MessageProfileDto(
                                friend.getUsername(),
                                true,
                                null,
                                null,
                                null,
                                null,
                                0

                        ))));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            System.out.println("No sessions found for " + friend.getUsername());
        }
        return ResponseEntity.ok(true);
    }

    @Override
    public Message getById(Long messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    @Override
    public ResponseEntity<List<MessageProfileDto>> getAll(HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(
                messageRepository.findAllForMessgePage(user.getId()).stream().map(
                message -> {
                    Boolean isSenderI = message.getSender().getUsername().equals(user.getUsername());
                    User profile = isSenderI ? message.getReceiver() : message.getSender();
                    return new MessageProfileDto(
                            profile.getUsername(),
                            isSenderI,
                            profile.getFullName(),
                            profile.getPictureBase64(),
                            message.getContent(),
                            message.getTime(),
                            messageRepository.countBySeenEqualsAndReceiverEqualsAndSenderEquals(0, message.getReceiver(), message.getSender())
                    );
                }).toList()
        );
    }
}