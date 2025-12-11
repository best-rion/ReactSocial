package com.hossainrion.ReactSocial.messaging.service;

import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.messaging.dto.MessageProfileDto;
import com.hossainrion.ReactSocial.messaging.dto.MessageToReceiveDto;
import com.hossainrion.ReactSocial.messaging.dto.MessageToSendDto;
import com.hossainrion.ReactSocial.service.UserService;
import com.hossainrion.ReactSocial.utils.Util;
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
    private final UserService userService;

    CustomHandler(MessageService service, UserService userService) {
        this.messageService = service;
        this.userService = userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = (String) session.getAttributes().get("owner");
        if (username != null) {
            sessionManager.addSession(username, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException {

        MessageToReceiveDto receivedMessage = Util.toObject(message.getPayload(), MessageToReceiveDto.class);

        String username = (String) session.getAttributes().get("owner");
        String friend = (String) session.getAttributes().get("dedicatedTo");

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
            if (s == null || !s.isOpen()) {
                sessions.remove(s);
                return;
            }
            try {
                String receiver = (String) s.getAttributes().get("dedicatedTo");
                if (receiver.equals(dedicatedTo)) {
                    s.sendMessage(new TextMessage(Util.toJsonString(messageToSend)));
                }
                if (receiver.equals("MESSAGES")) {
                    boolean isSenderI = messageToSend.sender().equals(owner);
                    User user = userService.getUserByUsername(dedicatedTo);
                    s.sendMessage(new TextMessage(Util.toJsonString(
                            new MessageProfileDto(
                                    user.getUsername(),
                                    isSenderI,
                                    user.getFullName(),
                                    user.getPictureBase64(),
                                    messageToSend.text(),
                                    messageToSend.timestamp(),
                                    messageToSend.seen() ? 0 : 1
                            )
                    )));
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = (String) session.getAttributes().get("owner");
        if (username != null) {
            sessionManager.removeSession(username,session);
        }
    }
}
