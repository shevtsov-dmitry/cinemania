package ru.video_material.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.video_material.PATH;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.video_material.PATH.*;

//@WebMvcTest(VideoController.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VideoControllerTest {

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
        String url = ENDPOINT_URL + "/delete/byId/"  + randomId;

        mockMvc.perform(delete(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo(STR."Deletion failed. The video with id \{randomId} doesn't exist in GridFS.")));
    }

    private static MockMultipartFile prepareVideoFileToUpload() throws IOException {
        String contentType = "multipart/form-data";
        String name = "video";
        String filename = STR."\{name}.mp4";
        Path path = Paths.get(ASSETS_PATH + filename);
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile("file", filename, contentType, content);
    }


    String generateRandomHash() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[10];
        secureRandom.nextBytes(randomBytes);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(randomBytes);
        byte[] hashBytes = md.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
