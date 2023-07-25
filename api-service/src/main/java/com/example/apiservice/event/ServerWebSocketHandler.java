package com.example.apiservice.event;

import com.example.apiservice.config.SessionStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;


@Slf4j
public class ServerWebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable {

    private final SessionStore sessionStore;

    public ServerWebSocketHandler(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Server connection has been established with session id: " + session.getId());
        sessionStore.addSession(session);
        TextMessage message = new TextMessage("test message from server");
        log.info("Server sends: {}", message);
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Server connection closed: {}", status);
        sessionStore.removeSession(session);
        log.info("Session with id: " + session.getId() + " has been removed");
    }

    //@Scheduled(fixedRate = 10000)
    void sendPeriodicMessages() throws IOException {
        log.info("Number of sessions: {}", sessionStore.getSessions().size());
        for (WebSocketSession session : sessionStore.getSessions().values()) {
            if (session.isOpen()) {
                String broadcast = "server periodic message " + LocalTime.now();
                log.info("Server sends: {}", broadcast);
                session.sendMessage(new TextMessage(broadcast));
            }
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String request = message.getPayload();
        log.info("Server received: {}", request);
        String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));
        log.info("Server sends: {}", response);
        session.sendMessage(new TextMessage(response));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info("Server transport error: {}", exception.getMessage());
    }

    @Override
    public List<String> getSubProtocols() {
        return Collections.singletonList("subprotocol.demo.websocket");
    }
    
}
