package ru.video_material.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;

@Configuration
@EnableWebMvc
public class WebConfig implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173", "http://192.168.0.106:5173/")
                .allowedMethods("GET");
    }
}
