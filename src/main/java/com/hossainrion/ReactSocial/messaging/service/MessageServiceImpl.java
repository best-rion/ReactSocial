package com.hossainrion.ReactSocial.messaging.service;

import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.messaging.dto.MessageProfileDto;
import com.hossainrion.ReactSocial.messaging.dto.MessageToSendDto;
import com.hossainrion.ReactSocial.messaging.entity.Message;
import com.hossainrion.ReactSocial.messaging.repository.MessageRepository;
import com.hossainrion.ReactSocial.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public List<MessageToSendDto> getMessages(String from, HttpServletRequest request) {
        User friend = userService.getUserByUsername(from);
        User me = userService.getCurrentUser(request);

        List<Message> friendAndMe = messageRepository.findAllBySenderAndReceiver(friend, me);
        List<Message> meAndFriend = messageRepository.findAllBySenderAndReceiver(me, friend);
        return Stream.concat(friendAndMe.stream(), meAndFriend.stream()).sorted(Comparator.comparing(Message::getTime)).map(MessageToSendDto::of).toList();
    }

    @Override
    public void setSeen(Long friendId, HttpServletRequest request) {
        User thisUser = userService.getCurrentUser(request);
        User friend = userService.getUserById(friendId);
        messageRepository.setSeenBySenderIdAndReceiverId(friendId, thisUser.getId());

        List<WebSocketSession> sessions = sessionManager.getSessions(friend.getUsername());
        if (sessions != null && !sessions.isEmpty()) {
            sessions.forEach(s -> {

                String receiver = (String) s.getAttributes().get("friend");
                if (! receiver.equals(thisUser.getUsername())) return;

                try {
                    s.sendMessage(new TextMessage("seen" + thisUser.getUsername()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            System.out.println("No sessions found for " + friend.getUsername());
        }
    }

    @Override
    public Message getById(Long messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    @Override
    public List<MessageProfileDto> getAll(HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        return messageRepository.findAllForMessgePage(user.getId()).stream().map(
                message -> {
                    Boolean isSender = user.getUsername().equals(message.getReceiver().getUsername());
                    User profile = isSender ? message.getSender() : message.getReceiver();
                    return new MessageProfileDto(
                            profile.getUsername(),
                            isSender,
                            profile.getFullName(),
                            profile.getPictureBase64(),
                            message.getContent(),
                            message.getTime(),
                            messageRepository.countBySeenEqualsAndReceiverEqualsAndSenderEquals(0, message.getReceiver(), message.getSender())
                    );
                }
        ).toList();
    }
}