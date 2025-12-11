package com.hossainrion.ReactSocial.messaging;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    // Key: username, Value: WebSocketSession
    private final ConcurrentHashMap<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    public void addSession(String username, WebSocketSession session) {
        if (!sessions.containsKey(username)) {
            List<WebSocketSession> sessionList = Collections.synchronizedList(new ArrayList<>());
            sessionList.add(session);
            sessions.put(username, sessionList);
        } else {
            sessions.get(username).add(session);
        }
    }

    public void removeSession(String username, WebSocketSession session) {
        sessions.get(username).remove(session);
        if (sessions.get(username).isEmpty()) {
            sessions.remove(username);
        }
    }

    public List<WebSocketSession> getSessions(String username) {
        return sessions.get(username);
    }
}
