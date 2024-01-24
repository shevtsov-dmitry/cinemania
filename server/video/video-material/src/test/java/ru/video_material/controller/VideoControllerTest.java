package ru.video_material.controller;

import com.google.gson.Gson;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import ru.video_material.model.VideoMetadata;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
import static ru.video_material.CONSTANTS.*;

@WebMvcTest(VideoController.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VideoControllerTest {
    @Autowired
    MockMvc mockMvc;
    final String PARENT_PATH = "/videos";
    static String metadataId;

    @Test
    @Order(1)
    void saveAllVideoMaterialInformation() throws Exception {
        var videoMaterial = new VideoMetadata("Never catch me", "2022-01-23", "USA", "comedy", 12);
        videoMaterial.setVideoId(generateRandomHash());
        videoMaterial.setPosterId(generateRandomHash());
        videoMaterial.setRating(7.53F);
        String url = PARENT_PATH + "/save-metadata";
        Gson gson = new Gson();
        String JSON = gson.toJson(videoMaterial);
        mockMvc.perform(post(url)
                        .content(JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())))
                .andDo(result -> metadataId = result.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void deleteById() throws Exception {
        String url = STR."\{PARENT_PATH}/delete/byId/\{metadataId}";
        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(content().string(metadataId));
    }

    @Test
    @Order(3)
    void deleteById_whenId_notFound() throws Exception {
        String url = STR."\{PARENT_PATH}/delete/byId/\{metadataId}";
        String answer = STR."Deletion failed. Entity with id \{metadataId} not found.";
        mockMvc.perform(delete(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(answer));
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

    String videoId = null;
    @Test
    @Order(4)
    void uploadVideo_thenCheckIfWasUploaded() throws Exception {
        MockMultipartFile preparedFile = prepareVideoFileToUpload();
        String url = PARENT_PATH + "/upload/one";
        mockMvc.perform(multipart(url)
                        .file(preparedFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType("plain/text"))
                .andExpect(content().string(not(emptyString())))
                .andDo(result -> videoId = result.getResponse().getContentAsString());
    }

    @Test
    @Order(5)
    void deleteSavedVideo() throws Exception {
        assertNotNull(videoId);
        String url = PARENT_PATH + "/delete/byId/" + videoId;
        mockMvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void tryToDeleteNonExistentVideo() throws Exception {
        String randomId = generateRandomHash();
        String url = PARENT_PATH + "/delete/byId/"  + randomId;
        mockMvc.perform(delete(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("plain/text"))
                .andExpect(content().string(equalTo(STR."Deletion failed. Entity with id \{randomId} not found.")));
    }

    private static MockMultipartFile prepareVideoFileToUpload() throws IOException {
        String contentType = "multipart/form-data";
        String name = "video";
        String filename = STR."\{name}.mp4";
        Path path = Paths.get(ASSETS_PATH + filename);
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile("file", filename, contentType, content);
    }
}