package ru.video_material;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.video_material.controller.PosterControllerTest;
import ru.video_material.controller.VideoMetadataControllerTest;
import ru.video_material.model.ContentMetadata;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServiceTest {

    @Autowired
    MockMvc mockMvc;

    static String videoId;
    static String posterId;
    static String metadataId;

    @Test
    @Order(1)
    void uploadVideo() throws Exception {
        MockMultipartFile preparedVideo = VideoMetadataControllerTest.prepareVideoFileToUpload();
        final String url = "/videos/upload";

        mockMvc.perform(multipart(url)
                        .file(preparedVideo))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andDo(result -> videoId = result.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void uploadPoster() throws Exception {
        final String url = "/videos/posters/upload";
        var posterTest = new PosterControllerTest();

        mockMvc.perform(multipart(url)
                        .file(posterTest.fileJPEG))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andDo(result -> posterId = result.getResponse().getContentAsString());
    }

    @Test
    @Order(3)
    void saveMetadata() throws Exception {
        assertNotNull(videoId);
        assertNotNull(posterId);

        var videoMetadata = new ContentMetadata(
                "Star wars",
                "2022-10-30",
                "USA",
                "SCI-FI",
                12,
                posterId,
                videoId,
                7.67F
        );
        Gson gson = new Gson();
        String json = gson.toJson(videoMetadata);
        final String url = "/videos/save/metadata";

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andDo(result -> metadataId = result.getResponse().getContentAsString());
    }

    @Test
    @Order(4)
    void deleteVideo_AfterSuccessfullyUploaded() throws Exception {
        String url = "/videos/delete/byId/" + videoId;

        mockMvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void deletePoster_AfterSuccessfullyUploaded() throws Exception {
        String url = "/videos/posters/delete/byId/" + posterId;

        mockMvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void deleteMetadata_AfterSuccessfullyUploaded() throws Exception {
        String url = "/videos/delete/metadata/byId/" + metadataId;

        mockMvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    // !

    @Test
    void havingEmptyMetadataBody_tryToSaveIt() throws Exception {
        String url = "/videos/save/metadata";

        mockMvc.perform(post(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Required request body is missing or invalid."));
    }

    @Test
    void deleteVideoWithNonExistentId() throws Exception {

    }

}