package ru.streaming.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VideoControllerTest {
    @Autowired
    MockMvc mockMvc;

    final static String HOST = "http://localhost:8080";

    // ! To execute tests you need to contain video file into assets package and specify filename
    final String filepath = "src/test/java/com/videostorage/assets";
    final String filename = "video.mp4";
    @Test
    @Order(1)
    void saveVideo() throws Exception {
        Path path = Paths.get(filepath + "/" + filename);
        byte[] content = Files.readAllBytes(path);
        String contentType = "multipart/form-data";
        String shortFileName = "funny-video";
        MockMultipartFile videoFile = new MockMultipartFile("file", shortFileName, contentType, content);
        final String url = HOST + "/upload/one";
        mockMvc.perform(multipart(url)
                        .file(videoFile)
                        .param("title", shortFileName))
                .andExpect(status().isOk())
                .andExpect(content().contentType("plain/text"));
    }

    @Test
    void getMapping() throws Exception {
        mockMvc.perform(get(HOST + "/"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void streamVideo() {
        Assertions.assertTrue(null);
    }

    @Test
    @Order(3)
    void deleteVideo() {
        Assertions.assertTrue(null);
    }

}