package ru.video_material;

import jdk.jfr.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.video_material.model.Video;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.video_material.CONSTANTS.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompleteAppTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    void uploadVideo() throws Exception {
        final Video video = new Video();

        final String url = CONSTANTS.HOST_AND_PORT + "/videos/save-metadata";
        mockMvc.perform(post(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("plain/text"));
    }

    @Test
    @Order(3)
    void saveMetadata(){
        final String url = STR."\{HOST_AND_PORT}/videos/upload/one";
    }

    @Test
    @Order(5)
    void savePoster(){

    }
}