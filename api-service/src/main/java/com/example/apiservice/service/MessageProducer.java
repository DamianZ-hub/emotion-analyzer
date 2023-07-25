package com.example.apiservice.service;

import com.example.apiservice.data.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class MessageProducer {

    String topicName = "exampleTopic";

    private KafkaTemplate<String, GenericMessage> messageTemplate;
    private ObjectMapper objectMapper;

    @Autowired
    public MessageProducer(KafkaTemplate<String, GenericMessage> messageTemplate, ObjectMapper objectMapper) {
        this.messageTemplate = messageTemplate;
        this.objectMapper = objectMapper;

    }

    @Async
    public void sendMessage(Request request) {

        String requestJson = null;
        try {
            requestJson = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while deserializing JSON: " + e.getMessage());
        }
        GenericMessage requestMessage = new GenericMessage<>(requestJson);

        CompletableFuture<SendResult<String, GenericMessage>> future = messageTemplate.send(topicName, requestMessage);
        String finalRequestJson = requestJson;
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[" + finalRequestJson +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                log.error("Unable to send message=[" +
                        finalRequestJson + "] due to : " + ex.getMessage());
            }
        });

    }
//    @Async
//    public CompletableFuture<Void> start() {
//        log.info("start");
//        return CompletableFuture.allOf()
//                .thenAccept(__ -> log.info("finish"));
//    }
}
