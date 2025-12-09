package com.hossainrion.ReactSocial.utils;

import com.hossainrion.ReactSocial.messaging.SessionManager;
import com.hossainrion.ReactSocial.messaging.dto.MessageToReceiveDto;
import com.hossainrion.ReactSocial.messaging.dto.MessageToSendDto;
import com.hossainrion.ReactSocial.messaging.service.MessageService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

import static com.hossainrion.ReactSocial.utils.Util.sessionManager;


@Component
public class CustomHandler extends TextWebSocketHandler{

    private final MessageService messageService;

    CustomHandler(MessageService service) {
        this.messageService = service;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            sessionManager.addSession(username, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException {

        MessageToReceiveDto receivedMessage = Util.toObject(message.getPayload(), MessageToReceiveDto.class);

        String username = (String) session.getAttributes().get("username");
        String friend = (String) session.getAttributes().get("friend");

        MessageToSendDto messageToSend = MessageToSendDto.of(
                messageService.saveMessage(username, friend, receivedMessage.text())
        );

        sendMessage(friend, username, messageToSend); // To friend

        Thread.sleep(500);

        sendMessage(username, friend, MessageToSendDto.of(messageService.getById(messageToSend.id()))); // To self
    }

    private void sendMessage(String owner, String dedicatedTo, MessageToSendDto messageToSend) {
        if (owner == null) return;
        List<WebSocketSession> sessions = sessionManager.getSessions(owner);
        if (sessions == null || sessions.isEmpty()) return;
        sessions.forEach(s -> {
            try {
                String receiver = (String) s.getAttributes().get("friend");
                if (! receiver.equals(dedicatedTo) ) return;

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
