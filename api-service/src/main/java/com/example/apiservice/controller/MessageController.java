package com.example.apiservice.controller;

import com.example.apiservice.data.Message;
import com.example.apiservice.exceptions.MessageException;
import com.example.apiservice.service.MessageProducer;
import com.netflix.discovery.EurekaClient;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@NoArgsConstructor
@Slf4j
@RequestMapping(path = "/messages")
public class MessageController {

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @Autowired
    @Qualifier("websocket-source-topic")
    private NewTopic websocketSourceTopic;

    //OAuth 2.0 would be enough for this
    @MessageMapping("/request")
    public void sendMessage(
            @Payload Message[] messages,
            SimpMessageHeaderAccessor headerAccessor
    ) throws InterruptedException, MessageException {
        Thread.sleep(1000);
        String simpSessionId = headerAccessor.getSessionId();
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        log.info("SimpSession ID: {}", simpSessionId);
        log.info("Session ID: {}", sessionId);
        for(Message message : messages)
            messageProducer.sendMessage(message, websocketSourceTopic.name());

    }



}
