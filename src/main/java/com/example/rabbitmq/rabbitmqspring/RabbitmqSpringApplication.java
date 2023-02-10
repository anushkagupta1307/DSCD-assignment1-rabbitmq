package com.example.rabbitmq.rabbitmqspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RabbitmqSpringApplication {

	//Server 1 has 3 queues- queueA-client 1 , queueB-client 2 , queueC-client 3
	//Server 2 has 3 queues- queueD-client 1 , queueE-client 2 , queueF-client 3

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqSpringApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}


}
