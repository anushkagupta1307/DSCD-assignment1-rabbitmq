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

    @Bean(name="queueA")
    Queue producer1Queue1(){
        return new Queue("queueA",false);
    }

    @Bean(name="queueB")
    Queue producer1Queue2(){
        return new Queue("queueB",false);
    }


    @Bean(name="queueC")
    Queue producer2Queue1(){
        return new Queue("queueC",false);
    }

    @Bean(name="queueD")
    Queue producer2Queue2(){
        return new Queue("queueD",false);
    }

    @Bean(name="exchangeA")
    DirectExchange directExchange1(){
        return new DirectExchange("exchangeA");
    }

    @Bean(name="exchangeB")
    DirectExchange directExchange2(){
        return new DirectExchange("exchangeB");
    }

    @Bean
    Binding bindingQueueA(Queue queueA, DirectExchange exchangeA){
        return BindingBuilder.bind(queueA).to(exchangeA).with("routing.key");
    }

    @Bean
    Binding bindingQueueB(Queue queueB, DirectExchange exchangeA){
        return BindingBuilder.bind(queueB).to(exchangeA).with("routing.key");
    }

    @Bean
    Binding bindingQueueC(Queue queueC, DirectExchange exchangeB){
        return BindingBuilder.bind(queueC).to(exchangeB).with("routing.key");
    }

    @Bean
    Binding bindingQueueD(Queue queueD, DirectExchange exchangeB){
        return BindingBuilder.bind(queueD).to(exchangeB).with("routing.key");
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
