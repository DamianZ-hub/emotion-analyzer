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
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@NoArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
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


    @MessageMapping("/request")
    public void sendMessage(
            @Payload Message[] messages,
            SimpMessageHeaderAccessor headerAccessor
    ) throws InterruptedException, MessageException {
        Thread.sleep(1000);

        String simpSessionId = headerAccessor.getSessionId().toString();
        String username = headerAccessor.getSessionAttributes().get("username").toString();
        List<String> authorities = (List<String>) headerAccessor.getSessionAttributes().get("authorities");
        String userId = headerAccessor.getSessionAttributes().get("userId").toString();
        String httpSessionId = headerAccessor.getSessionAttributes().get("httpSessionId").toString();

        log.info("SimpSession ID: {}", simpSessionId);
        log.info("HttpSessionId ID: {}", httpSessionId);

        for(Message message : messages) {

            message.setUserId(username);

            log.info("Message: {" + message.getContent() + "} from user: {" + message.getUserId() + "} with messageId {" + message.getMessageId() + "}");

            messageProducer.sendMessage(message, websocketSourceTopic.name());
        }
    }



}
