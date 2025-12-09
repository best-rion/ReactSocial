package com.hossainrion.ReactSocial.messaging.service;

import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.messaging.dto.MessageToSendDto;
import com.hossainrion.ReactSocial.messaging.entity.Message;
import com.hossainrion.ReactSocial.messaging.repository.MessageRepository;
import com.hossainrion.ReactSocial.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    MessageServiceImpl(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @Override
    public Message sendMessage(String from, String to, String message) {
        Message msg = new Message();
        msg.setSender(userService.getUserByUsername(from));
        msg.setReceiver(userService.getUserByUsername(to));
        msg.setContent(message);
        msg.setTime(new Date());
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
}