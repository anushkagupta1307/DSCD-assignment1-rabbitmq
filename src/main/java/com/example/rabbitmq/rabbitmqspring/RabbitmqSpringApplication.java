package com.example.rabbitmq.rabbitmqspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RabbitmqSpringApplication {

	//Server 1 has 2 queues- Queue A for client 1 and Queue B for client 2
	//Server 2 has 2 queues - Queue C for client 1 and Queue D for client 2

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqSpringApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}


}
