package com.video_material.controller;

import com.google.gson.Gson;
import com.video_material.CONSTANTS;
import com.video_material.model.VideoMaterial;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VideoMaterialControllerTest {
    @Autowired
    MockMvc mockMvc;
    final String ENDPOINT_URL = STR."\{CONSTANTS.HOST_AND_PORT}/video-materials";

    @Test
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
        var videoMaterial = new VideoMaterial("Never catch me", "2022-01-23", "USA", "comedy", 12);
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
                .andExpect(res -> Long.parseLong(res.getResponse().getContentAsString()));
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