package com.example.rabbitmq.rabbitmqspring.consumer;

import com.example.rabbitmq.rabbitmqspring.model.Article;
import com.example.rabbitmq.rabbitmqspring.model.PublishArticle;
import com.example.rabbitmq.rabbitmqspring.model.PublishArticleRequest;
import com.example.rabbitmq.rabbitmqspring.model.RegistryServerConnectionRequest;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
public class Client3 {

    public static List<String> client3_serversList=new ArrayList<>();

    public static void main(String[] args) {
        int n=0;
        while (n != 6) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Press 1 to get List of Servers : ");
            System.out.println("Press 2 to Join a Server : ");
            System.out.println("Press 3 to Leave a Server : ");
            System.out.println("Press 4 to Get Articles : ");
            System.out.println("Press 5 to Publish Article to Server : ");
            System.out.println("Press 6 to Exit ");
            n = sc.nextInt();

            switch (n) {
                case 1:
                    String uri = "http://localhost:8080/registry-server/list-of-servers";
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<List<RegistryServerConnectionRequest>> response = restTemplate.exchange(uri,
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<RegistryServerConnectionRequest>>() {
                            });
                    List<RegistryServerConnectionRequest> listOfServers = response.getBody();
                    for(int i=0;i<listOfServers.size();i++){
                        if(listOfServers.get(i).getServer_name().equals("queueA")) {
                            listOfServers.get(i).setServer_name("queueC");
                            listOfServers.get(i).setRouting_key("routing.C");
                        }
                        if(listOfServers.get(i).getServer_name().equals("queueD")) {
                            listOfServers.get(i).setServer_name("queueF");
                            listOfServers.get(i).setRouting_key("routing.F");
                        }
                    }
                    for (int i = 0; i < listOfServers.size(); i++) {
                        if(listOfServers.get(i).getServer_name().equals("queueC")){
                            System.out.print("Server 1 : ");
                        }else if(listOfServers.get(i).getServer_name().equals("queueF")){
                            System.out.print("Server 2 : ");
                        }
                        System.out.println(listOfServers.get(i).getServer_name() + " " + listOfServers.get(i).getRouting_key());
                        System.out.println();
                    }
                    break;
                case 2:
                    System.out.println("Enter Server name to join : ");
                    String server_name_join = sc.next();
                    System.out.println("Enter routing key of server : ");
                    String routing_key_join = sc.next();
                    HttpHeaders headers = new HttpHeaders();
                    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    int client_id_join = 3;
                    HttpEntity<Integer> entity = new HttpEntity<>(client_id_join, headers);
                    if(server_name_join.equals("queueC")){
                        System.out.println(new RestTemplate().exchange(
                                "http://localhost:8080/server1/join-server", HttpMethod.POST, entity, String.class).getBody());
                    }
                    else if(server_name_join.equals("queueF")){
                        System.out.println(new RestTemplate().exchange(
                                "http://localhost:8080/server2/join-server", HttpMethod.POST, entity, String.class).getBody());
                    }
                    client3_serversList.add(server_name_join);
                    break;
                case 3:
                    System.out.println("Enter Server name to Leave : ");
                    String server_name_leave = sc.next();
                    System.out.println("Enter Routing Key of Server : ");
                    String routing_key_leave = sc.next();
                    HttpHeaders headers1 = new HttpHeaders();
                    headers1.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    int client_id_leave = 3;
                    HttpEntity<Integer> entity1 = new HttpEntity<>(client_id_leave, headers1);
                    if(server_name_leave.equals("queueC")) {
                        System.out.println(new RestTemplate().exchange("http://localhost:8080/server1/leave-server",
                                HttpMethod.POST, entity1, String.class).getBody());
                    }
                    else if(server_name_leave.equals("queueF")){
                        System.out.println(new RestTemplate().exchange("http://localhost:8080/server2/leave-server",
                                HttpMethod.POST, entity1, String.class).getBody());
                    }
                    client3_serversList.remove(server_name_leave);
                    break;
                case 4:
                    System.out.println("Enter Server name to Get Articles : ");
                    String server_name_getarticle=sc.next();
                    System.out.println("Enter Routing Key of Server : ");
                    String routing_key_getarticle=sc.next();
                    System.out.println("Enter the TYPE of Articles : (Enter NA if you do not wish to specify)" );
                    String type_get= sc.next();
                    System.out.println("Enter the DATE after which you want articles (Format : YYYY-MM-DD) : (Enter NA if you do not wish to specify)");
                    String date_get= sc.next();
                    System.out.println("Enter Name of Author : (Enter NA if you do not wish to specify)");
                    String author_get=sc.next();
                    if(client3_serversList.contains(server_name_getarticle)){
                        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
                        connectionFactory.setUsername("guest");
                        connectionFactory.setPassword("guest");
                        MessageConverter messageConverter = new Jackson2JsonMessageConverter();
                        RabbitTemplate rabbit = new RabbitTemplate(connectionFactory);
                        rabbit.setMessageConverter(messageConverter);
                        List<Article> articles = new ArrayList<>();
                        if (new RabbitAdmin(rabbit).getQueueInfo(server_name_getarticle).getMessageCount() == 0) {
                            System.out.println("No New Messages on Server yet. ");
                        } else {
                            while (new RabbitAdmin(rabbit).getQueueInfo(server_name_getarticle).getMessageCount() != 0) {
                                Article article = processArticle(rabbit, server_name_getarticle);
                                if (article != null && article.getArticle_data() != null) {
                                    articles.add(article);
                                }
                            }
                        }
                        List<Article> filteredArticles = Article.filterArticles(articles, type_get, date_get, author_get);
                        articles.removeAll(filteredArticles);
                        for (int i = 0; i < filteredArticles.size(); i++) {
                            System.out.println("TYPE : " + filteredArticles.get(i).getType());
                            System.out.println("Publishing Date : " + filteredArticles.get(i).getDate());
                            System.out.println("Author : " + filteredArticles.get(i).getAuthor());
                            System.out.println("Article : " + filteredArticles.get(i).getArticle_data());
                            System.out.println();
                        }
                        String urlappend=new String();
                        if(server_name_getarticle.equals("queueC"))
                            urlappend="server1";
                        else if(server_name_getarticle.equals("queueF"))
                            urlappend="server2";

                        for(int i=0;i<articles.size();i++){
                            PublishArticle publishArticle=new PublishArticle();
                            publishArticle.setArticle(articles.get(i));
                            publishArticle.setRouting_key(routing_key_getarticle);
                            HttpHeaders headers2 = new HttpHeaders();
                            headers2.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                            HttpEntity<PublishArticle> entity2 = new HttpEntity<>(publishArticle, headers2);
                            new RestTemplate().exchange(
                                    "http://localhost:8080/"+urlappend+"/post-article", HttpMethod.POST, entity2, String.class).getBody();
                        }
                    }else{
                        System.out.println("Cannot get articles from server to which you are not subscribed.");
                    }
                    break;
                case 5:
                    System.out.println("Enter server name to which you want to Publish : ");
                    String server_publish = sc.next();
                    System.out.println("Enter Routing key of server to which you want to Publish : ");
                    String routing_key_publish = sc.next();
                    System.out.println("Enter Type of Article : ");
                    String type = sc.next();
                    System.out.println("Enter Date in (YYYY-MM-DD) : ");
                    String date = sc.next();
                    System.out.println("Enter Author : ");
                    String author = sc.next();
                    sc.nextLine();
                    System.out.println("Enter Article : ");
                    String article_data = sc.nextLine();
                    HttpHeaders headers2 = new HttpHeaders();
                    PublishArticleRequest publishArticleRequest=new PublishArticleRequest();
                    Article article = new Article();
                    article.setType(type);
                    article.setDate(date);
                    article.setArticle_data(article_data);
                    article.setAuthor(author);
                    publishArticleRequest.setArticle(article);
                    publishArticleRequest.setClient_id(3);
                    publishArticleRequest.setRouting_key(routing_key_publish);
                    headers2.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    HttpEntity<PublishArticleRequest> entity2 = new HttpEntity<>(publishArticleRequest, headers2);
                    if(server_publish.equals("queueC")) {
                        System.out.println(new RestTemplate().exchange(
                                "http://localhost:8080/server1/post-article/v2", HttpMethod.POST, entity2, String.class).getBody());

                        PublishArticle p1=new PublishArticle();
                        p1.setArticle(article);
                        p1.setRouting_key("routing.A");
                        HttpHeaders headers3 = new HttpHeaders();
                        headers3.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                        HttpEntity<PublishArticle> entity3 = new HttpEntity<>(p1, headers3);
                        new RestTemplate().exchange(
                                "http://localhost:8080/server1/post-article", HttpMethod.POST, entity3, String.class).getBody();

                        PublishArticle p2=new PublishArticle();
                        p2.setArticle(article);
                        p2.setRouting_key("routing.B");
                        HttpHeaders headers4 = new HttpHeaders();
                        headers4.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                        HttpEntity<PublishArticle> entity4 = new HttpEntity<>(p2, headers4);
                        new RestTemplate().exchange(
                                "http://localhost:8080/server1/post-article", HttpMethod.POST, entity4, String.class).getBody();
                    }
                    else if(server_publish.equals("queueF")){
                        System.out.println(new RestTemplate().exchange(
                                "http://localhost:8080/server2/post-article/v2", HttpMethod.POST, entity2, String.class).getBody());

                        PublishArticle p3=new PublishArticle();
                        p3.setArticle(article);
                        p3.setRouting_key("routing.D");
                        HttpHeaders headers5 = new HttpHeaders();
                        headers5.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                        HttpEntity<PublishArticle> entity5 = new HttpEntity<>(p3, headers5);
                        new RestTemplate().exchange(
                                "http://localhost:8080/server2/post-article", HttpMethod.POST, entity5, String.class).getBody();

                        PublishArticle p4=new PublishArticle();
                        p4.setArticle(article);
                        p4.setRouting_key("routing.E");
                        HttpHeaders headers6 = new HttpHeaders();
                        headers6.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                        HttpEntity<PublishArticle> entity6 = new HttpEntity<>(p4, headers6);
                        new RestTemplate().exchange(
                                "http://localhost:8080/server2/post-article", HttpMethod.POST, entity6, String.class).getBody();
                    }
                    break;
                case 6 :
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
