package com.example.processingservice.serde;

import com.example.processingservice.data.Message;
import com.example.processingservice.data.ResponseMessage;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public final class CustomSerdes {

    private CustomSerdes() {}

    public static Serde<Message> messageSerde() {
        JsonSerializer<Message> serializer = new JsonSerializer<>();
        JsonDeserializer<Message> deserializer = new JsonDeserializer<>(Message.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<ResponseMessage> responseMessageSerde() {
        JsonSerializer<ResponseMessage> serializer = new JsonSerializer<>();
        JsonDeserializer<ResponseMessage> deserializer = new JsonDeserializer<>(ResponseMessage.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

}