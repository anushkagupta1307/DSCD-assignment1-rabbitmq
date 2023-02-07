package com.example.rabbitmq.rabbitmqspring.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RegistryServerConnectionRequest {

    private String server_name;
    private String routing_key;
}
