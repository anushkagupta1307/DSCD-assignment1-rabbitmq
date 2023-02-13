package com.example.rabbitmq.rabbitmqspring.consumer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class StartClient3 {

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
                    Client.getServerList(3);
                    break;
                case 2:
                    String server_name_join=Client.joinServer(3);
                    client3_serversList.add(server_name_join);
                    break;
                case 3:
                    String server_name_leave=Client.leaveServer(3);
                    client3_serversList.remove(server_name_leave);
                    break;
                case 4:
                    Client.getArticles(client3_serversList);
                    break;
                case 5:
                      Client.publishArticle(3);
                    break;
                case 6 :
                    System.exit(0);
                    break;
            }

        }
    }



}
