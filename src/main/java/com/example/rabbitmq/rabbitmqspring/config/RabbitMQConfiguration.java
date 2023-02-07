package com.example.rabbitmq.rabbitmqspring.config;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RabbitMQConfiguration {

    @Bean(name="producer1")
    Queue producer1Queue(){
        return new Queue("producer1",false);
    }

    @Bean(name="producer2")
    Queue producer2Queue(){
        return new Queue("producer2",false);
    }

    @Bean(name="exchange1")
    DirectExchange directExchange1(){
        return new DirectExchange("exchange1");
    }

    @Bean(name="exchange2")
    DirectExchange directExchange2(){
        return new DirectExchange("exchange2");
    }

    @Bean
    Binding bindingProducer1Queue(Queue producer1, DirectExchange exchange1){
        return BindingBuilder.bind(producer1).to(exchange1).with("producer1.key");
    }

    @Bean
    Binding bindingProducer2Queue(Queue producer2, DirectExchange exchange2){
        return BindingBuilder.bind(producer2).to(exchange2).with("producer2.key");
    }

    @Bean
    MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory factory){
        RabbitTemplate rabbitTemplate=new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
