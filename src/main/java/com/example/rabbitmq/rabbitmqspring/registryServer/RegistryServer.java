package com.example.rabbitmq.rabbitmqspring.registryServer;

import com.example.rabbitmq.rabbitmqspring.model.RegistryServerConnectionRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RegistryServer {

    public static int MAX_SERVERS=5;
    public static List<RegistryServerConnectionRequest> allServers=new ArrayList<>();

   @GetMapping("/registry-server/list-of-servers")
    public static List<RegistryServerConnectionRequest> getListOfServers(){
      return allServers;
   }

   @PostMapping("/registry-server/connect")
   public static String connectToRegistryServer(@RequestBody RegistryServerConnectionRequest registryServerConnectionRequest){
        if(allServers.size()<=MAX_SERVERS){
            allServers.add(registryServerConnectionRequest);
            return "Connected to Registry Server "+registryServerConnectionRequest.getServer_name()+" "+registryServerConnectionRequest.getRouting_key();
        }else{
            return "Max Capacity Reached. Cannot Connect more servers.";
        }
   }
}
