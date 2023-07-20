package com.example.apiservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class MessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);

//    @Async
//    public CompletableFuture<Void> start() {
//        log.info("start");
//        return CompletableFuture.allOf()
//                .thenAccept(__ -> log.info("finish"));
//    }
}
