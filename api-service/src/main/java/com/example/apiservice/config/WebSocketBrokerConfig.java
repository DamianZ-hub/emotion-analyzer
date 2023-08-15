package com.example.apiservice.config;

import com.example.apiservice.interceptor.HttpHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer  {

    @Value("${broker.relay.host}")
    private String brokerRelayHost;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .addInterceptors(getHandshakeInterceptor());
        registry.addEndpoint("/sockjs")
                .setAllowedOrigins("*")
                .addInterceptors(getHandshakeInterceptor())
                .withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableStompBrokerRelay("/queue", "/topic")
                .setRelayHost(brokerRelayHost);
    }



    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
//        registration.setMessageSizeLimit(8192);
//        registration.setSendBufferSizeLimit(8192);
        registration.setMessageSizeLimit(18192);
        registration.setSendBufferSizeLimit(18192);
    }

    @Bean
    public HandshakeInterceptor getHandshakeInterceptor() {
        return new HttpHandshakeInterceptor();
    }

}
