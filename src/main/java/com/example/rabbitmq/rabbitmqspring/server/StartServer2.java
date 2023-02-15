package com.example.rabbitmq.rabbitmqspring.server;

import com.example.rabbitmq.rabbitmqspring.model.Article;
import com.example.rabbitmq.rabbitmqspring.model.PublishArticle;
import com.example.rabbitmq.rabbitmqspring.model.PublishArticleRequest;
import com.example.rabbitmq.rabbitmqspring.model.RegistryServerConnectionRequest;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@RestController
public class StartServer2 {
    public static int SERVER2_MAX_CLIENTS=2;
    public static List<Integer> server2_clients=new ArrayList<>();
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    @Qualifier("exchangeB")
    private DirectExchange exchangeB;

    public static void main(String[] args) {
        int n = 0;
        while (n != 3) {

            System.out.println("Press 1 to Connect to Registry Server : ");
            System.out.println("Press 2 to Connect to Another Server : ");
            Scanner sc = new Scanner(System.in);
            n = sc.nextInt();

            switch(n) {
                case 1 :
                    Server.registerWithRegistryServer(2);
                    break;
                case 2 :
                    Server.becomeClient();
                    break;
                case 3:
                    System.exit(0);
                    break;

            }
        }
    }

    @PostMapping("/server2/post-article")
    public String send(@RequestBody PublishArticle publishArticle){
        rabbitTemplate.convertAndSend(exchangeB.getName(), publishArticle.getRouting_key(),publishArticle.getArticle());
        return "Message sent to queue.";
    }

    @PostMapping("/server2/post-article/v2")
    public String sendArticle(@RequestBody PublishArticleRequest publishArticleRequest){
        System.out.println("Publish Article Request from Client : "+publishArticleRequest.getClient_id());
        if(server2_clients.contains(publishArticleRequest.getClient_id())) {
            rabbitTemplate.convertAndSend(exchangeB.getName(), publishArticleRequest.getRouting_key(), publishArticleRequest.getArticle());
            return "SUCCESS. Message sent to queue.";
        }else{
            return "FAILED. This client is not subscribed to this server.";
        }
    }

    @PostMapping("/server2/join-server")
    public String joinServer(@RequestBody int client_id){
        if(server2_clients.size()<SERVER2_MAX_CLIENTS){
            server2_clients.add(client_id);
            return "SUCCESS. ADDED CLIENT "+client_id;
        }
        else{
            return "FAILED. Max capacity reached. Cannot add more clients.";
        }
    }

    @PostMapping("/server2/leave-server")
    public String leaveServer(@RequestBody int client_id){
        if(server2_clients.contains(client_id)){
            server2_clients.remove(Integer.valueOf(client_id));
            return "SUCCESS. REMOVED CLIENT "+client_id;
        }
        else{
            return "FAILED. No Such Client present on this server.";
        }
    }
}
