package com.example.apiservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${websocket-source-topic-name}")
    String webSocketSourceTopicName;

    @Value(value = "${response-websocket-source-topic-name}")
    String responseWebsocketSourceTopicName;

    @Value(value = "${rest-api-source-topic-name}")
    String ApiSourceTopicName;

    @Value(value = "${partitions-number}")
    int partitionsNumber;

    @Value(value = "${replication-factor}")
    int replicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    @Qualifier("websocket-source-topic")
    public NewTopic websocketSourceTopic() {
        return TopicBuilder
                .name(webSocketSourceTopicName)
                .partitions(partitionsNumber)
                .replicas(replicationFactor)
                .build();
    }

    @Bean
    @Qualifier("response-websocket-source-topic")
    public NewTopic responseWebsocketSourceTopic() {
        return TopicBuilder
                .name(responseWebsocketSourceTopicName)
                .partitions(partitionsNumber)
                .replicas(replicationFactor)
                .build();
    }

    @Bean
    @Qualifier("rest-api-source-topic")
    public NewTopic restApiSourceTopic() {
        return TopicBuilder
                .name(ApiSourceTopicName)
                .partitions(partitionsNumber)
                .replicas(replicationFactor)
                .build();
    }

}