package com.example.apiservice.controller;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/utils")
public class UtilsController {

    @Autowired
    @Qualifier("websocket-source-topic")
    private NewTopic websScketSourceTopic;

    @Autowired
    @Qualifier("response-websocket-source-topic")
    private NewTopic responseWebsScketSourceTopic;

    @GetMapping("/websocket-topic-info")
    public Map<String, Object> getWebSocketTopicInfo() {

        return Map.of(
                "topic-name", websScketSourceTopic.name(),
                "partitions", websScketSourceTopic.numPartitions(),
                "replicationFactor", websScketSourceTopic.replicationFactor()
        );
    }

    @GetMapping("/response-websocket-topic-info")
    public Map<String, Object> getResponseWebSocketTopicInfo() {

        return Map.of(
                "topic-name", responseWebsScketSourceTopic.name(),
                "partitions", responseWebsScketSourceTopic.numPartitions(),
                "replicationFactor", responseWebsScketSourceTopic.replicationFactor()
        );
    }


   @GetMapping("/user")
    public String index(Principal principal) {
        return principal.getName();
    }

//    @GetMapping("/test")
//    public Mono<String> test(Principal principal, BearerTokenAuthentication auth) {
//
//        return Mono.just("test");
//    }

    @GetMapping("/test")
    public String test(Principal principal) {

        return new String("test");
    }

}
