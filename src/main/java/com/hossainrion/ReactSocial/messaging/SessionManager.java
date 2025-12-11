package com.hossainrion.ReactSocial.messaging;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    // Key: username, Value: WebSocketSession
    private final ConcurrentHashMap<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        if (session == null) return;
        String username = (String) session.getAttributes().get("owner");
        if (username == null) {
            closeConnection(session);
            return;
        };

        if (!sessions.containsKey(username)) {
            List<WebSocketSession> sessionList = Collections.synchronizedList(new ArrayList<>());
            sessionList.add(session);
            sessions.put(username, sessionList);
        } else {
            sessions.get(username).add(session);
        }
    }

    public void removeSession(WebSocketSession session) {
        if (session == null) return;
        String username = (String) session.getAttributes().get("owner");
        if (username == null) {
            closeConnection(session);
            return;
        }

        if (!sessions.containsKey(username)) return;

        closeConnection(session);
        sessions.get(username).remove(session);

        if (sessions.get(username).isEmpty()) {
            sessions.remove(username);
        }
    }

    public List<WebSocketSession> getSessions(String username) {
        return sessions.get(username);
    }

    private void closeConnection(WebSocketSession session) {
        if (session != null && session.isOpen()) {
            try {session.close();} catch (Exception e) {e.printStackTrace();}
        }
    }
}
