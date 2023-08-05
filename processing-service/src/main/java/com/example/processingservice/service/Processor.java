package com.example.processingservice.service;

import com.example.processingservice.communication.Communication;
import com.example.processingservice.data.Message;
import com.example.processingservice.data.ResponseMessage;
import com.example.processingservice.serde.CustomSerdes;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;

@Service
@Slf4j
public class Processor {

    private final Communication communication;

    @Value(value = "${bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${application-id-config}")
    private String applicationIdConfig;

    @Autowired
    public Processor(Communication communication) {
        this.communication = communication;
    }

    public void test () {


        Properties streamProperties = new Properties();
        streamProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationIdConfig);
        streamProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        final StreamsBuilder builder = new StreamsBuilder();

        final Map<String, Object> webSocketTopicInfo = communication.getWebSocketTopicInfo();
        final Map<String, Object> responseWebSocketTopicInfo = communication.getResponseWebSocketTopicInfo();

        final String webSocketTopicName = (String) webSocketTopicInfo.get("topic-name");
        final String responseWebSocketTopicName = (String) responseWebSocketTopicInfo.get("topic-name");

        KStream<String, Message> inputStream = builder.stream(webSocketTopicName, Consumed.with(Serdes.String(), CustomSerdes.messageSerde()));
        KStream<String, ResponseMessage> midStream = inputStream.mapValues(ResponseMessage::responseMessage);

        midStream.mapValues((key, value) -> {
            log.info("Processing message: {}", value.toString());
            String recognizedEmotion = emotionRecognize(value.getContent());
            value.setEmotion(recognizedEmotion);
            return value;
        }).to(responseWebSocketTopicName, Produced.with(Serdes.String(),
                CustomSerdes.responseMessageSerde()));

//        midStream.to(responseWebSocketTopicName, Produced.with(Serdes.String(),
//                CustomSerdes.responseMessageSerde()));

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamProperties);
        kafkaStreams.start();



    }

    //TODO: as python service
    private String emotionRecognize(String content) {
        return "0";
    }

}
