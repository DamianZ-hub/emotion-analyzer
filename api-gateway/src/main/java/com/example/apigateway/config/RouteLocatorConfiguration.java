package com.example.apigateway.config;


import com.example.apigateway.filter.CustomPreAuthenticationFilter;
import com.example.apigateway.filter.PassUserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfiguration {

    @Autowired
    PassUserFilter passUserFilter;

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec
                        .path("/utils/**")
                        .filters(f -> f.filter(passUserFilter))
                        .uri("lb://api-service")
                )
                .route(predicateSpec -> predicateSpec
                        .path("/ws/**")
                        .filters(f -> f.filter(passUserFilter))
                        .uri("lb:ws://api-service")
                       // .uri("ws://api-service/ws")

                )
                .route(predicateSpec -> predicateSpec
                        .path("/sockjs/**")
                        .filters(f -> f.filter(passUserFilter))
                        .uri("lb:ws://api-service")
                        //.uri("ws://api-service/sockjs")

                )
                .build();
    }
}
