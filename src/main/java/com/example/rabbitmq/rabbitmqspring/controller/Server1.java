package com.example.rabbitmq.rabbitmqspring.controller;

import com.example.rabbitmq.rabbitmqspring.model.Article;

import com.example.rabbitmq.rabbitmqspring.model.RegistryServerConnectionRequest;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    public static int MAX_CLIENTS=5;

    public List<Integer> clients=new ArrayList<>();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("exchange1")
    private DirectExchange exchange1;

    @PostMapping("/server1/post-article")
    public String send(@RequestBody Article article){
         rabbitTemplate.convertAndSend(exchange1.getName(), "producer1.key",article);
         return "Message sent to queue.";
    }

    @PostMapping("/server1/join-server")
    public String joinServer(@RequestBody int client_id){
        if(clients.size()<=MAX_CLIENTS){
            clients.add(client_id);
            return "SUCCESS. ADDED CLIENT "+client_id;
        }
        else{
            return "FAILED. Max capacity reached. Cannot add more clients.";
        }
    }

    @PostMapping("/server1/leave-server")
    public String leaveServer(@RequestBody int client_id){
        if(clients.contains(client_id)){
            clients.remove(Integer.valueOf(client_id));
            return "SUCCESS. REMOVED CLIENT "+client_id;
        }
        else{
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

            switch(n) {
                case 1 :
                    HttpHeaders headers = new HttpHeaders();
                    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    RegistryServerConnectionRequest registryServerConnectionRequest = new RegistryServerConnectionRequest();
                    registryServerConnectionRequest.setServer_name("producer1");
                    registryServerConnectionRequest.setRouting_key("producer1.key");
                    HttpEntity<RegistryServerConnectionRequest> entity = new HttpEntity<>(registryServerConnectionRequest, headers);
                    System.out.println(new RestTemplate().exchange(
                            "http://localhost:8080/registry-server/connect", HttpMethod.POST, entity, String.class).getBody());
                break;
                case 2:
                    break;
                case 3:
                    System.exit(0);
                    break;

            }
        }
    }


}
