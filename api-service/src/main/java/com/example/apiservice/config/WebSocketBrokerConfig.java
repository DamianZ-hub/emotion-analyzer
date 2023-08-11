package com.example.apiservice.config;

import com.example.apiservice.interceptor.HttpHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
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
//        registry
//                .addEndpoint("/ws")
//                .addInterceptors(handshakeInterceptor())
//                .withSockJS();
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*");
        registry.addEndpoint("/sockjs")
                .setAllowedOrigins("*")
                .withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        //registry.enableSimpleBroker("/queue");
        registry.enableStompBrokerRelay("/queue", "/topic")
                .setRelayHost(brokerRelayHost);
    }



    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(8192);
        registration.setSendBufferSizeLimit(8192);
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new HttpHandshakeInterceptor();
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor =
//                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    List<String> authorization = accessor.getNativeHeader("X-Authorization");
//                    log.debug("X-Authorization: {}", authorization);
//
//                }
//                return message;
//            }
//        });
//    }

}
