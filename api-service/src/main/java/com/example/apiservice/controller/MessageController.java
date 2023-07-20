package com.example.apiservice.controller;

import com.example.apiservice.data.Request;
import com.example.apiservice.data.Response;
import com.example.apiservice.service.MessageProducer;
import com.netflix.discovery.EurekaClient;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@NoArgsConstructor
@RequestMapping(path = "/messages")
public class MessageController {

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @Autowired
    private MessageProducer messageProducer;

    //OAuth 2.0 would be enough for this
    @MessageMapping("/request")
    @SendTo("/topic/responses")
    public Response request(
            @Payload Request[] request
    ) throws InterruptedException{
        Thread.sleep(2000);
        return Response.builder()
                .userId(request[0].getUserId())
                .messageId(request[0].getMessageId())
                .response("1")
                .build();
    }


//    @GetMapping("/async1")
//    public CompletableFuture<Map<String, String>> get() {
//        log.info("get - principal: {}", "a");
//        return messageProducer.start()
//                .thenApply(__ -> Map.of("username", principal.getName()));
//    }

}
