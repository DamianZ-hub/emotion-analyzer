package com.example.processingservice;

import com.example.processingservice.service.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class ProcessingServiceApplication {



	public static void main(String[] args) {
		ApplicationContext context =  SpringApplication.run(ProcessingServiceApplication.class, args);

		Processor processor = context.getBean(Processor.class);

		processor.test();
	}

}
