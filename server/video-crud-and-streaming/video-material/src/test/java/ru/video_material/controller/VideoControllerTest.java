package ru.video_material.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.video_material.COMMON.*;

//@WebMvcTest(VideoController.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VideoControllerTest {

    @Autowired
    MockMvc mockMvc;
    final String ENDPOINT_URL = HOST_AND_PORT + "/videos";
    static String videoId;

    @Test
    @Order(1)
    void uploadVideo_thenCheckIfWasUploaded() throws Exception {
        MockMultipartFile preparedFile = prepareVideoFileToUpload();
        String url = ENDPOINT_URL + "/upload";

        mockMvc.perform(multipart(url)
                .file(preparedFile)
                .contentType("video/mp4")
                .param("title", generateRandomHash().substring(0, 5)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(not(emptyString())))
                .andDo(result -> videoId = result.getResponse().getContentAsString());
    }

    @Test
    @Order(3)
    void deleteSavedVideo() throws Exception {
        assertNotNull(videoId);
        String url = ENDPOINT_URL + "/delete/byId/" + videoId;

        mockMvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void tryToDeleteNonExistentVideo() throws Exception {
        String randomId = generateRandomHash();
        String url = ENDPOINT_URL + "/delete/byId/" + randomId;

        mockMvc.perform(delete(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        equalTo("Deletion failed. The video with id %s doesn't exist in GridFS.".formatted(randomId))));
    }

    public static MockMultipartFile prepareVideoFileToUpload() throws IOException {
        String contentType = "multipart/form-data";
        String name = "video";
        String filename = "%s.mp4".formatted(name);
        Path path = Paths.get(ASSETS_PATH + filename);
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile("file", filename, contentType, content);
    }

}
