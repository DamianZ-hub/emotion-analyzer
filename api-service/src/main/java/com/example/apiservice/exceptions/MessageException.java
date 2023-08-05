package com.example.apiservice.exceptions;


import com.example.apiservice.data.Message;

public class MessageException extends Exception {

    private Message message;

    public MessageException(String errorMessage, Message message) {
        super(errorMessage);
        this.message = message;
    }

    public Message getExceptionMessage() {
        return Message.builder()
                .userId(message.getUserId())
                .messageId(message.getMessageId())
                .content(getMessage())
                .build();
    }
}
