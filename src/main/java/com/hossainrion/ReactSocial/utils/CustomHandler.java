package com.hossainrion.ReactSocial.utils;

import com.hossainrion.ReactSocial.messaging.dto.MessageToReceiveDto;
import com.hossainrion.ReactSocial.messaging.dto.MessageToSendDto;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

import static com.hossainrion.ReactSocial.utils.Util.sessionManager;

public class CustomHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            sessionManager.addSession(username, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String username = (String) session.getAttributes().get("username");

        MessageToReceiveDto receivedMessage = Util.toObject(message.getPayload(), MessageToReceiveDto.class);

        MessageToSendDto messageToSend = new MessageToSendDto(username, receivedMessage.text(), receivedMessage.timestamp());

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
