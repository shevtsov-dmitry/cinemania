package com.content_assist_with_input;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ContentAssistWithInputApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentAssistWithInputApplication.class, args);
    }

        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(@NonNull CorsRegistry registry) {
                    registry.addMapping("/film-info-genre").allowedOrigins("http://localhost:3000");
                }
            };
        }

}
