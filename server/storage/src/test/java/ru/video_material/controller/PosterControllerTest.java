package ru.storage.controller;

import org.bson.assertions.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.bson.assertions.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.storage.COMMON.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// @Rollback(value = false)
class PosterControllerTest {

    @Autowired
    MockMvc mockMvc;

    private final String ENDPOINT_URL = HOST_AND_PORT.concat("/posters");

    // JPEG
    static String idJPEG;

    // PNG
    // static String idPNG;
    // String namePNG = "png-image";
    // String filenamePNG = "%s.png".formatted(namePNG);
    // Path pathPNG = Paths.get(ASSETS_PATH + filenamePNG);
    // byte[] contentPNG = Files.readAllBytes(pathPNG);
    // MockMultipartFile filePNG = new MockMultipartFile("file", filenamePNG,
    // contentType, contentPNG);

    String imagesContentType = "multipart/form-data";

    String filenameJPEG = "sin-city-poster.jpg";
    Path pathJPEG = Paths.get(ASSETS_PATH + filenameJPEG);
    byte[] contentJPEG = Files.readAllBytes(pathJPEG);
    MockMultipartFile fileJPEG = new MockMultipartFile("file", filenameJPEG, imagesContentType, contentJPEG);

    PosterControllerTest() throws IOException {
    }

    @Test
    @Order(1)
    public void uploadJPEGImage() throws Exception {

        String url = "%s/upload".formatted(ENDPOINT_URL);
        mockMvc.perform(multipart(url)
                .file(fileJPEG))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())))
                .andDo(res -> idJPEG = res.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    public void afterUpload_checkJPEGImageInDatabase() throws Exception {
        assertNotNull(idJPEG);
        String url = "%s/get/recent/1".formatted(ENDPOINT_URL);

        List<String> jsonFields = List.of("videoId", "videoId", "poster", "rating", "age", "subGenres",
                "mainGenre", "country", "releaseDate", "title", "metadataId");

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andDo(res -> System.out.println(res.getResponse().getContentAsString()))
                .andExpect(content().bytes(contentJPEG));
    }

}
