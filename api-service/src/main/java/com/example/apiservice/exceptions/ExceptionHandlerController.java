package com.example.apiservice.exceptions;

import com.example.apiservice.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.ControllerAdvice;

//@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @MessageExceptionHandler(MessageException.class)
    @SendTo("/queue/exception")
    public Message handleMessageException(MessageException exception) {
        log.error(exception.getMessage());
        return exception.getExceptionMessage();
    }

    @MessageExceptionHandler(Exception.class)
    @SendTo("/queue/exception")
    public Message handleException(Exception exception) {
        log.error("Exception: {}", exception.getMessage());
        return new Message(null, null, exception.getMessage());
    }

}
