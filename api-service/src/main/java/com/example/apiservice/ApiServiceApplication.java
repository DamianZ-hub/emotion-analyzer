package com.example.apiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@EnableScheduling
@EnableWebSocketMessageBroker
public class ApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiServiceApplication.class, args);
	}

}
