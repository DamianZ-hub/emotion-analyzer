package com.example.apiservice.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private final static String X_AUTH_TOKEN = "x-auth-token";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

            String token = servletRequest.getHeaders().get(X_AUTH_TOKEN).toString();
            String username = servletRequest.getHeaders().get("username").toString();
            String authorities = servletRequest.getHeaders().get("authorities").toString();
            String userId = servletRequest.getHeaders().get("userid").toString();

            HttpSession session = servletRequest.getServletRequest().getSession();


            username = username.substring(1, username.length() - 1);
            userId = userId.substring(1, userId.length() - 1);
            token = token.substring(1, token.length() - 1);
            List<String> authoritiesList = Arrays.asList(authorities.substring(2, authorities.length() - 2).split(", "));

            attributes.put("username", username);
            attributes.put("authorities", authoritiesList);
            attributes.put("userId", userId);
            attributes.put("httpSessionId", session.getId());
            attributes.put("token", token);

        }
        return true;
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
    }
}