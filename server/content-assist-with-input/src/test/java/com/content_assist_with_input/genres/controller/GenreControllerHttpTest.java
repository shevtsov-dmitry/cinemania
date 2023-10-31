package com.content_assist_with_input.genres.controller;

import com.content_assist_with_input.genres.repo.GenreRepo;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback(value = true)
class GenreControllerHttpTest {
    // * EXPECTED TESTS
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GenreRepo repo;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String requestMappingUrl = "http://localhost:8080/film-info-genre";

    @Test
    void addOneGenre() throws Exception {
        final String urlMapping = "add-new-genre";
        final String param = "genre";
        String value = "戏剧";
        if(repo.findByName("戏剧") != null)
            value = randomStringValue();
        final String url = "%s/%s?%s=%s".formatted(requestMappingUrl,urlMapping,param,value);
        log.info("url: {}", url);
        mockMvc.perform(request(HttpMethod.POST,url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("add new genre successfully."));
    }



    @Test
    void addOneGenreMoreThanOnce() throws Exception {
        final String urlMapping = "add-new-genre";
        final String param = "genre";
        String value = "喜剧";
        if(repo.findByName("戏剧") != null)
            value = randomStringValue();
        final String url = "%s/%s?%s=%s".formatted(requestMappingUrl,urlMapping,param,value);
        log.info("url: {}", url);
        mockMvc.perform(request(HttpMethod.POST,url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("add new genre successfully."));

        mockMvc.perform(request(HttpMethod.POST,url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Cannot save because already exist in database."));

    }

    private String randomStringValue() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < random.nextInt(5,10); i++) {
            char ch = (char) random.nextInt(100);
            stringBuilder.append(ch);
        }
        return stringBuilder.toString();
    }

}


