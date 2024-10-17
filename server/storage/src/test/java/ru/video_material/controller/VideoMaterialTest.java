package ru.storage.controller;

import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.storage.model.ContentMetadata;

import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// @Rollback(false)
public class VideoMaterialTest {

    @Autowired
    public MockMvc mockMvc;
    public Gson gson = new Gson();

    static String posterId;
    private String videoId;

    @Test
    // last in order
    public void save() throws Exception {
        var metadata = new ContentMetadata();
        metadata.setId(randomString(20));
        metadata.setTitle(randomString(10));
        metadata.setReleaseDate("2014-03-12T13:37:27+00:00");
        metadata.setCountry(randomString(10));
        metadata.setMainGenre(randomString(7));
        metadata.setSubGenres(List.of(randomString(10), randomString(10), randomString(10)));
        metadata.setAge(16);
        metadata.setRating((float) (10 * Math.random()));

        assertNotNull(posterId);
        assertNotNull(videoId);
        metadata.setPosterId(posterId);
        metadata.setVideoId(videoId);

        String url = "/videos/metadata/save";
        mockMvc.perform(post(url)
                .content(gson.toJson(metadata)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(not(emptyString())));
    }

    public String randomString(int length) {
        String someTextChars = "abcdefjhklnopqrstuvwxyz1234567890";
        Random random = new Random();
        var sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(someTextChars.charAt(random.nextInt(someTextChars.length())));
        }
        return sb.toString();
    }
}
