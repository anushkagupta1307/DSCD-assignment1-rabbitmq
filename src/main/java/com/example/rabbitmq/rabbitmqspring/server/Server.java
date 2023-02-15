package com.example.rabbitmq.rabbitmqspring.server;

import com.example.rabbitmq.rabbitmqspring.model.Article;
import com.example.rabbitmq.rabbitmqspring.model.PublishArticle;
import com.example.rabbitmq.rabbitmqspring.model.RegistryServerConnectionRequest;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Server {
    public static void becomeClient(){
        Scanner sc=new Scanner(System.in);
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
        if(server.equals("queueA")||server.equals("queueB") || server.equals("queueC"))
            urlappend="server1";
        if(server.equals("queueD")||server.equals("queueE") || server.equals("queueF"))
            urlappend="server2";


        for(int i=0;i<articles.size();i++){
            PublishArticle publishArticle=new PublishArticle();
            publishArticle.setArticle(articles.get(i));
            publishArticle.setRouting_key(routing_key);
            HttpHeaders headers2 = new HttpHeaders();
            headers2.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<PublishArticle> entity2 = new HttpEntity<>(publishArticle, headers2);
            new RestTemplate().exchange(
                    "http://localhost:8080/"+urlappend+"/post-article", HttpMethod.POST, entity2, String.class).getBody();

        }
        if(urlappend.equals("server2")) {
            for (int i = 0; i < articles.size(); i++) {

                PublishArticle p1 = new PublishArticle();
                p1.setArticle(articles.get(i));
                p1.setRouting_key("routing.A");
                HttpHeaders headers1 = new HttpHeaders();
                headers1.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity<PublishArticle> entity1 = new HttpEntity<>(p1, headers1);
                System.out.println(new RestTemplate().exchange(
                        "http://localhost:8080/server1/post-article", HttpMethod.POST, entity1, String.class).getBody());

                PublishArticle p2 = new PublishArticle();
                p2.setArticle(articles.get(i));
                p2.setRouting_key("routing.B");
                HttpHeaders headers2 = new HttpHeaders();
                headers2.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity<PublishArticle> entity2 = new HttpEntity<>(p2, headers2);
                new RestTemplate().exchange(
                        "http://localhost:8080/server1/post-article", HttpMethod.POST, entity2, String.class).getBody();

                PublishArticle p3 = new PublishArticle();
                p3.setArticle(articles.get(i));
                p3.setRouting_key("routing.C");
                HttpHeaders headers3 = new HttpHeaders();
                headers3.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity<PublishArticle> entity3 = new HttpEntity<>(p3, headers3);
                new RestTemplate().exchange(
                        "http://localhost:8080/server1/post-article", HttpMethod.POST, entity3, String.class).getBody();

            }
        }
        if(urlappend.equals("server1")){
            for(int i=0;i<articles.size();i++){

                PublishArticle p1=new PublishArticle();
                p1.setArticle(articles.get(i));
                p1.setRouting_key("routing.D");
                HttpHeaders headers1 = new HttpHeaders();
                headers1.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity<PublishArticle> entity1 = new HttpEntity<>(p1, headers1);
                System.out.println(new RestTemplate().exchange(
                        "http://localhost:8080/server2/post-article", HttpMethod.POST, entity1, String.class).getBody());

                PublishArticle p2=new PublishArticle();
                p2.setArticle(articles.get(i));
                p2.setRouting_key("routing.E");
                HttpHeaders headers2 = new HttpHeaders();
                headers2.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity<PublishArticle> entity2 = new HttpEntity<>(p2, headers2);
                new RestTemplate().exchange(
                        "http://localhost:8080/server2/post-article", HttpMethod.POST, entity2, String.class).getBody();

                PublishArticle p3=new PublishArticle();
                p3.setArticle(articles.get(i));
                p3.setRouting_key("routing.F");
                HttpHeaders headers3 = new HttpHeaders();
                headers3.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity<PublishArticle> entity3 = new HttpEntity<>(p3, headers3);
                new RestTemplate().exchange(
                        "http://localhost:8080/server2/post-article", HttpMethod.POST, entity3, String.class).getBody();

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

    public static void registerWithRegistryServer(int server_id){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        RegistryServerConnectionRequest registryServerConnectionRequest = new RegistryServerConnectionRequest();
        if(server_id==1) {
            registryServerConnectionRequest.setServer_name("queueA");
            registryServerConnectionRequest.setRouting_key("routing.A");
        }
        if(server_id==2){
            registryServerConnectionRequest.setServer_name("queueD");
            registryServerConnectionRequest.setRouting_key("routing.D");
        }
        HttpEntity<RegistryServerConnectionRequest> entity = new HttpEntity<>(registryServerConnectionRequest, headers);
        System.out.println(new RestTemplate().exchange(
                "http://localhost:8080/registry-server/connect", HttpMethod.POST, entity, String.class).getBody());
    }
}
