package com.example.apigateway.filter;


import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomPreAuthenticationFilter  implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)
    {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String query = uri.getQuery();
        if (query == null || query.isEmpty()) {
            return chain.filter(exchange);
        }

        Map<String, String> queryParams = parseQueryParams(query);
        String token = queryParams.get("token");

        if (token != null) {
            URI uriWithoutQuery = removeQueryParams(uri);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .headers(httpHeaders -> httpHeaders.add("x-auth-token", token))
                    .uri(uriWithoutQuery)
                    .build();
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();

            return chain.filter(modifiedExchange);
        }

        return chain.filter(exchange);
    }
    private static Map<String, String> parseQueryParams(String query) {

        Map<String, String> queryParams = new HashMap<>();
        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            }
        }

        return queryParams;
    }

    private URI removeQueryParams(URI uri) {
        try {
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, uri.getFragment());
        } catch (Exception e) {
            return uri;
        }
    }

}
