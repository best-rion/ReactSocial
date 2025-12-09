package com.hossainrion.ReactSocial.utils;

import com.hossainrion.ReactSocial.messaging.SessionManager;
import com.hossainrion.ReactSocial.messaging.dto.MessageToReceiveDto;
import com.hossainrion.ReactSocial.messaging.dto.MessageToSendDto;
import com.hossainrion.ReactSocial.messaging.entity.Message;
import com.hossainrion.ReactSocial.messaging.repository.MessageRepository;
import com.hossainrion.ReactSocial.messaging.service.MessageService;
import com.hossainrion.ReactSocial.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
import java.util.List;


@Component
public class CustomHandler extends TextWebSocketHandler{

    private final MessageService messageService;
    private final SessionManager sessionManager;

    CustomHandler(MessageService service, SessionManager sessionManager) {
        this.messageService = service;
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            sessionManager.addSession(username, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        MessageToReceiveDto receivedMessage = Util.toObject(message.getPayload(), MessageToReceiveDto.class);

        String username = (String) session.getAttributes().get("username");

        Message msg = messageService.sendMessage(username, receivedMessage.recipient(), receivedMessage.text());

        MessageToSendDto messageToSend = new MessageToSendDto(msg.getId(), username, receivedMessage.text(), msg.getTime().toString());

        sendMessage(username, messageToSend);
        sendMessage(receivedMessage.recipient(), messageToSend);
    }

    private void sendMessage(String username, MessageToSendDto messageToSend) {
        if (username == null) return;
        List<WebSocketSession> sessions = sessionManager.getSessions(username);
        if (sessions == null || sessions.isEmpty()) return;
        sessions.forEach(s -> {
            try {
                s.sendMessage(new TextMessage(Util.toJsonString(messageToSend)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            sessionManager.removeSession(username,session);
        }
    }
}
