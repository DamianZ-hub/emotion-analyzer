package com.example.apigateway.filter;

import com.google.common.base.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class PassUserFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .flatMap(c -> {
                    JwtAuthenticationToken jwt = (JwtAuthenticationToken) c.getAuthentication();

                    String username = (String) jwt.getName();
                    String userId =  (String) jwt.getTokenAttributes().get("sub");
                    String authorities = jwt.getAuthorities().toString();

                    if (Strings.isNullOrEmpty(username)) {
                        return Mono.error(
                                new AccessDeniedException("Invalid token. User is not present in token.")
                        );
                    }

                    if (Strings.isNullOrEmpty(userId)) {
                        return Mono.error(
                                new AccessDeniedException("Invalid token. UserId is not present in token.")
                        );
                    }

                    if (Strings.isNullOrEmpty(authorities)) {
                        return Mono.error(
                                new AccessDeniedException("Invalid token. Authorities is not present in token.")
                        );
                    }


                    ServerHttpRequest request = exchange.getRequest().mutate()
//                            .headers(h -> jwt.getTokenAttributes().entrySet().forEach(
//                                    e -> h.add(e.getKey(), e.getValue().toString())
//                            ))
                            .header("username", username)
                            .header("authorities", authorities)
                            .header("userId", userId)
                            .build();

                    return chain.filter(exchange.mutate().request(request).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
