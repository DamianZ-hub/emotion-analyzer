package com.example.apiservice.config;


import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStore {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
    }

    public WebSocketSession getSessionById(String sessionId) {
        return sessions.get(sessionId);
    }

    public Map<String, WebSocketSession> getSessions() {
        return sessions;
    }
}
