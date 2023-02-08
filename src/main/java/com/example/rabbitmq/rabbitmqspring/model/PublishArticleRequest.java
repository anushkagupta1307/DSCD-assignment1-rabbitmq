package com.example.rabbitmq.rabbitmqspring.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PublishArticleRequest {

    private int client_id;
    private Article article;

}
