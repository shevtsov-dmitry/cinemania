package ru.video_material;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.video_material.controller.PosterControllerTest;
import ru.video_material.model.Poster;
import ru.video_material.model.Video;
import ru.video_material.model.VideoMetadata;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServiceTest {

    @Autowired
    MockMvc mockMvc;

    String videoId;
    String posterId;

    @Test
    @Order(1)
    void uploadVideo() throws Exception {
        final Video video = new Video();
        final String url = "/videos/upload";

        mockMvc.perform(post(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("plain/text"));
    }

    @Test
    @Order(2)
    void uploadPoster() throws Exception {
        final Poster poster = new Poster();
        final String url = "poster/upload";

        var posterTest = new PosterControllerTest();
        mockMvc.perform(multipart(url)
                .file(posterTest.fileJPEG))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andDo(result -> posterId = result.getResponse().getContentAsString());


    }

    @Test
    @Order(3)
    void saveMetadata(){
        var videoMetadata = new VideoMetadata(
                "Star wars",
                "2022-10-30",
                "USA",
                "SCI-FI",
                12,
                posterId,
                videoId,
                7.67F
        );
        final String url = "videos/save-metadata";
    }

    @Test
    @Order(4)
    void deleteVideo_AfterSuccessfullyUploaded(){

    }

    @Test
    @Order(4)
    void deletePoster_AfterSuccessfullyUploaded(){

    }

}