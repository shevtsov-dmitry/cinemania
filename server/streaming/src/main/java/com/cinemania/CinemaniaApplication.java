package com.cinemania;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.ServletRequest;

@SpringBootApplication
public class CinemaniaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaniaApplication.class, args);
    }

    @GetMapping("/")
    public String index(ServletRequest request) {
        return "Greetings from Spring Boot using protocol " + request.getProtocol() + "!";
    }

}
