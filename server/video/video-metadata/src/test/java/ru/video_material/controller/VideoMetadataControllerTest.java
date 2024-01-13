package ru.video_material.controller;

import com.google.gson.Gson;
import ru.video_material.CONSTANTS;
import ru.video_material.video.model.VideoMetadata;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VideoMetadataControllerTest {
    @Autowired
    MockMvc mockMvc;
    final String ENDPOINT_URL = STR."\{CONSTANTS.HOST_AND_PORT}/video-materials";
    static String id;

    @Test
    @Order(1)
    void saveAllVideoMaterialInformation() throws Exception {
   /*     {
            "filmName": "Lovely life",
                "releaseDate": "2014-03-12T13:37:27+00:00",
                "country": "Russia",
                "genre": "Drama",
                "age": 12,
                "imageUrl": "public/myimage.png",
                "watchTime": "10:34",
                "rating": 7.53
        }*/
        var videoMaterial = new VideoMetadata("Never catch me", "2022-01-23", "USA", "comedy", 12);
        videoMaterial.setVideoId(generateRandomHash());
        videoMaterial.setPosterId(generateRandomHash());
        videoMaterial.setRating(7.53F);
        String url = ENDPOINT_URL + "/save";
        Gson gson = new Gson();
        String JSON = gson.toJson(videoMaterial);
        mockMvc.perform(post(url)
                        .content(JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(res -> Long.parseLong(res.getResponse().getContentAsString()))
                .andDo(result -> id = result.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void deleteById() throws Exception {
        String url = STR."\{ENDPOINT_URL}/delete/byId/\{id}";
        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(content().string(id));
    }

    @Test
    @Order(3)
    void deleteById_whenId_notFound() throws Exception {
        String url = STR."\{ENDPOINT_URL}/delete/byId/\{id}";
        String answer = STR."Deletion failed. Entity with id \{id} not found.";
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

}