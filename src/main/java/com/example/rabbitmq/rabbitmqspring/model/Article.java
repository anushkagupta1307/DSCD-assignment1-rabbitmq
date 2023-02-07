package com.example.rabbitmq.rabbitmqspring.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Article {

    private String type;
    private String date;
    private String author;
    private String article_data;

    public static List<Article> filterArticles(List<Article> allArticles, String request_type, String request_date, String request_author){
        List<Article> filteredArticles=new ArrayList<>();

        if (!request_type.equals("NA") && !request_date.equals("NA") && !request_author.equals("NA")) {
            for (int i = 0; i < allArticles.size(); i++) {
                if (allArticles.get(i).getType().equals(request_type) && formatDate(allArticles.get(i).getDate()).isAfter(formatDate(request_date)) && allArticles.get(i).getAuthor().equals(request_author))
                    filteredArticles.add(allArticles.get(i));
            }
        } else if (!request_type.equals("NA") && !request_date.equals("NA")) {
            for (int i = 0; i < allArticles.size(); i++) {
                if (allArticles.get(i).getType().equals(request_type) && formatDate(allArticles.get(i).getDate()).isAfter(formatDate(request_date)))
                    filteredArticles.add(allArticles.get(i));
            }
        } else if (!request_type.equals("NA") && !request_author.equals("NA")) {
            for (int i = 0; i < allArticles.size(); i++) {
                if (allArticles.get(i).getType().equals(request_type) && allArticles.get(i).getAuthor().equals(request_author))
                    filteredArticles.add(allArticles.get(i));
            }
        } else if (!request_date.equals("NA") && !request_author.equals("NA")) {
            for (int i = 0; i < allArticles.size(); i++) {
                if (formatDate(allArticles.get(i).getDate()).isAfter(formatDate(request_date)) && allArticles.get(i).getAuthor().equals(request_author))
                    filteredArticles.add(allArticles.get(i));
            }
        } else if (!request_type.equals("NA")) {
            for (int i = 0; i < allArticles.size(); i++) {
                if (allArticles.get(i).getType().equals(request_type))
                    filteredArticles.add(allArticles.get(i));
            }

        } else if (!request_date.equals("NA")) {
            for (int i = 0; i < allArticles.size(); i++) {
                if (formatDate(allArticles.get(i).getDate()).isAfter(formatDate(request_date)))
                    filteredArticles.add(allArticles.get(i));
            }

        } else if (!request_author.equals("NA")) {
            for (int i = 0; i < allArticles.size(); i++) {
                if (allArticles.get(i).getAuthor().equals(request_author))
                    filteredArticles.add(allArticles.get(i));
            }
        } else {
            filteredArticles.addAll(allArticles);
        }

        return filteredArticles;
    }

    public static LocalDate formatDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return localDate;
    }

}
