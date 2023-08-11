package com.example.apiservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class SessionSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {

            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            //System.out.println(headerAccessor.getSessionAttributes().get("sessionId").toString());
            //log.info("Session subscribed: {}", event);

    }
}
