package com.example.processingservice.data;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ResponseMessage extends Message{

    private String emotion;

    public static ResponseMessage responseMessage(Message message) {
        return new ResponseMessage(message);
    }

    ResponseMessage(Message message, String emotion) {
        super(message.getUserId(), message.getMessageId(), message.getContent());
        this.emotion = emotion;
    }

    ResponseMessage(Message message) {
        this(message, null);
    }
}
