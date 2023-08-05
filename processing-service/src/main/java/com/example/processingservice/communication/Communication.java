package com.example.processingservice.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class Communication {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    public Map<String, Object> getWebSocketTopicInfo() {

        ServiceInstance serviceInstance = loadBalancerClient.choose("api-service");

        String uri = serviceInstance.getUri()+"/utils/websocket-topic-info";

        RestTemplate restTemplate = new RestTemplate();

        Map response = restTemplate.getForObject(uri, Map.class);

        return response;

    }

    public Map<String, Object> getResponseWebSocketTopicInfo() {

        ServiceInstance serviceInstance = loadBalancerClient.choose("api-service");

        String uri = serviceInstance.getUri()+"/utils/response-websocket-topic-info";

        RestTemplate restTemplate = new RestTemplate();

        Map response = restTemplate.getForObject(uri, Map.class);

        return response;

    }
}
