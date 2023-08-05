package com.example.apiservice.service;

import com.example.apiservice.data.Message;
import com.example.apiservice.exceptions.MessageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class MessageProducer {

    private final KafkaTemplate<String, Message> messageTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public MessageProducer(KafkaTemplate<String, Message> messageTemplate, ObjectMapper objectMapper, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageTemplate = messageTemplate;
        this.objectMapper = objectMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Async
    public void sendMessage(Message message, String topicName) throws MessageException {


        CompletableFuture<SendResult<String, Message>> future = messageTemplate.send(topicName, "senderUserId",message);
        future.whenComplete((result, ex) -> {
            sendResponseFromCluster(result, ex, message);
        });

    }

    private void sendResponseFromCluster(SendResult result, Throwable ex, Message message) {

        String messageJson = message.toString();

        if (ex == null) {
            String successContent = "Sent message=[" + messageJson +
                    "] with offset=[" + result.getRecordMetadata().offset() + "]";
            Message successMessage = Message.builder()
                    .userId(message.getUserId())
                    .messageId(message.getMessageId())
                    .content(successContent)
                    .build();

            simpMessagingTemplate.convertAndSend("/queue/send-success", successMessage);
            log.info(successContent);

        } else {
            String failureContent = "Unable to send message=[" +
                    messageJson + "] due to : " + ex.getMessage();
            Message failureMessage = Message.builder()
                    .userId(message.getUserId())
                    .messageId(message.getMessageId())
                    .content(failureContent)
                    .build();

            simpMessagingTemplate.convertAndSend("/queue/send-failure", failureMessage);
            log.error(failureContent);
        }
    }

}
