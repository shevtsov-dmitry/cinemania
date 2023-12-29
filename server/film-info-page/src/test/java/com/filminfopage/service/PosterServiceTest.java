package com.filminfopage.service;

import org.aspectj.lang.annotation.After;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback
class PosterServiceTest {

    @Autowired
    MockMvc mockMvc;
    final String endpointURL = "http://localhost:8080/film-info/poster";
    final String POSTERS_PATH = "src/test/java/com/filminfopage/assets/";
    String name = "sin-city-poster";
    String filename = STR."\{name}.jpg";
    Path path = Paths.get(POSTERS_PATH + filename);
    String contentType = "multipart/form-data";
    byte[] content = Files.readAllBytes(path);
    MockMultipartFile file = new MockMultipartFile("file", filename, contentType, content);
    static String id;

    PosterServiceTest() throws IOException {
    }

    @Test
    void uploadJPEGImage() throws Exception {
        String regex = "saved in database with id: [a-f0-9]+";
        String url = STR."\{endpointURL}/upload";
        mockMvc.perform(multipart(url)
                        .file(file)
                        .param("title", name))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(Matchers.matchesPattern(regex)))
                .andDo(result -> setId(result.getResponse().getContentAsString()));
    }

    void setId(String response) {
        String regex = "id: (\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        assertTrue(matcher.find(), STR."ID not found in response: \{response}");
        id = matcher.group(1);
    }

    @Test
    @After(value = "uploadJPEGImage")
    void getById() throws Exception {
        assertNotNull(id, "Id is null.");
        String url = STR."\{endpointURL}/get/byId/\{id}";
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    @After(value = "uploadJPEGImage")
    void getByTitle() throws Exception {
        String url = STR."\{endpointURL}/get/byTitle/\{name}";
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
    }

}