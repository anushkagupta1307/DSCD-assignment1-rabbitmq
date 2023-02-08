package com.example.rabbitmq.rabbitmqspring.controller;

import com.example.rabbitmq.rabbitmqspring.model.Article;

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
public class Server1 {

    public static int MAX_CLIENTS = 5;

    public static List<Integer> clients = new ArrayList<>();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("exchangeA")
    private DirectExchange exchangeA;

    @PostMapping("/server1/post-article")
    public String send(@RequestBody Article article) {
        rabbitTemplate.convertAndSend(exchangeA.getName(), "routing.key", article);
        return "Message sent to queue.";
    }

    @PostMapping("/server1/post-article/v2")
    public String sendArticle(@RequestBody PublishArticleRequest publishArticleRequest) {
        System.out.println("Publish Article Request from Client : " + publishArticleRequest.getClient_id());
        if (clients.contains(publishArticleRequest.getClient_id())) {
            rabbitTemplate.convertAndSend(exchangeA.getName(), "routing.key", publishArticleRequest.getArticle());
            return "SUCCESS. Message sent to queue.";
        } else {
            return "FAILED. Cannot publish since client is not subscribed.";
        }
    }

    @PostMapping("/server1/join-server")
    public String joinServer(@RequestBody int client_id) {
        System.out.println("Join Request from Client : " + client_id);
        if (clients.size() <= MAX_CLIENTS) {
            clients.add(client_id);
            return "SUCCESS. ADDED CLIENT " + client_id;
        } else {
            return "FAILED. Max capacity reached. Cannot add more clients.";
        }
    }

    @PostMapping("/server1/leave-server")
    public String leaveServer(@RequestBody int client_id) {
        System.out.println("Leave Request from Client : " + client_id);
        if (clients.contains(client_id)) {
            clients.remove(Integer.valueOf(client_id));
            return "SUCCESS. REMOVED CLIENT " + client_id;
        } else {
            return "FAILED. No Such Client present on this server.";
        }
    }

    public static void main(String[] args) {

        int n = 0;
        while (n != 3) {

            System.out.println("Press 1 to Connect to Registry Server : ");
            System.out.println("Press 2 to Connect to Another Server : ");
            Scanner sc = new Scanner(System.in);
            n = sc.nextInt();

            switch (n) {
                case 1:
                    HttpHeaders headers = new HttpHeaders();
                    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    RegistryServerConnectionRequest registryServerConnectionRequest = new RegistryServerConnectionRequest();
                    registryServerConnectionRequest.setServer_name("queueA");
                    registryServerConnectionRequest.setRouting_key("routing.key");
                    HttpEntity<RegistryServerConnectionRequest> entity = new HttpEntity<>(registryServerConnectionRequest, headers);
                    System.out.println(new RestTemplate().exchange(
                            "http://localhost:8080/registry-server/connect", HttpMethod.POST, entity, String.class).getBody());
                    break;
                case 2:
                    System.out.println("Enter Server Name of which you want to become client : ");
                    String server=sc.next();
                    System.out.println("Enter Routing key of server of which you want to become client : ");
                    String routing_key=sc.next();

                    CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
                    connectionFactory.setUsername("guest");
                    connectionFactory.setPassword("guest");
                    MessageConverter messageConverter = new Jackson2JsonMessageConverter();
                    RabbitTemplate rabbit = new RabbitTemplate(connectionFactory);
                    rabbit.setMessageConverter(messageConverter);
                    List<Article> articles = new ArrayList<>();
                    if(new RabbitAdmin(rabbit).getQueueInfo(server).getMessageCount() ==0){
                        System.out.println("No Messages on Server. ");
                    }else {
                        while (new RabbitAdmin(rabbit).getQueueInfo(server).getMessageCount() != 0) {
                            Article article = processArticle(rabbit, server);
                            if (article != null && article.getArticle_data() != null) {
                                articles.add(article);
                            }
                        }
                    }

                    String urlappend=new String();
                    if(server.equals("queueC")||server.equals("queueD"))
                        urlappend="server2";

                    for(int i=0;i<articles.size();i++){
                        HttpHeaders headers2 = new HttpHeaders();
                        headers2.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                        HttpEntity<Article> entity2 = new HttpEntity<>(articles.get(i), headers2);
                        new RestTemplate().exchange(
                                "http://localhost:8080/"+urlappend+"/post-article", HttpMethod.POST, entity2, String.class).getBody();

                    }
                    for(int i=0;i<articles.size();i++){
                        HttpHeaders headers2 = new HttpHeaders();
                        headers2.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                        HttpEntity<Article> entity2 = new HttpEntity<>(articles.get(i), headers2);
                        System.out.println(new RestTemplate().exchange(
                                "http://localhost:8080/server1/post-article", HttpMethod.POST, entity2, String.class).getBody());

                    }
                    break;
                case 3:
                    System.exit(0);
                    break;

            }
        }
    }
    public static Article processArticle(RabbitTemplate rabbit, String server_name_getarticle){
        try {
            Article message = (Article) rabbit.receiveAndConvert(server_name_getarticle);
            return message;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Exception caught in process Article method");
            return new Article();
        }
    }
}
