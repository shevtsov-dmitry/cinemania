package ru.video_material.controller;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.video_material.COMMON.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PosterControllerTest {
    @Autowired
    MockMvc mockMvc;
    @LocalServerPort
    private int PORT;
    final String ENDPOINT_URL = UriComponentsBuilder.fromHttpUrl(HOST).port(PORT).path("videos/posters").toUriString();

    String contentType = "multipart/form-data";

    // JPEG
    static String idJPEG;
    String nameJPEG = "sin-city-poster";
    String filenameJPEG = STR."\{nameJPEG}.jpg";
    Path pathJPEG = Paths.get(ASSETS_PATH + filenameJPEG);
    byte[] contentJPEG = Files.readAllBytes(pathJPEG);
    public MockMultipartFile fileJPEG = new MockMultipartFile("file", filenameJPEG, contentType, contentJPEG);


    // PNG
    static String idPNG;
    String namePNG = "png-image";
    String filenamePNG = STR."\{namePNG}.png";
    Path pathPNG = Paths.get(ASSETS_PATH + filenamePNG);
    byte[] contentPNG = Files.readAllBytes(pathPNG);
    MockMultipartFile filePNG = new MockMultipartFile("file", filenamePNG, contentType, contentPNG);

    public PosterControllerTest() throws IOException {
    }

    @Test
    @Order(1)
    public void uploadJPEGImage(MockMultipartFile fileJPEG) throws Exception {
        String url = STR."\{ENDPOINT_URL}/upload";
        mockMvc.perform(multipart(url)
                        .file(this.fileJPEG))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andDo(result -> idJPEG = result.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void uploadPNGImage() throws Exception {
        String url = STR."\{ENDPOINT_URL}/upload";
        mockMvc.perform(multipart(url)
                        .file(filePNG))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andDo(result -> idPNG = result.getResponse().getContentAsString());
    }

    @Test
    @Order(3)
    void getById() throws Exception {
        assertNotNull(idJPEG);
        assertNotNull(idPNG);

        String url = STR."\{ENDPOINT_URL}/get/byId/\{idJPEG}";
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));

        // * in case if PNG file was uploaded client gets JPEG file anyway
        url = STR."\{ENDPOINT_URL}/get/byId/\{idPNG}";
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    @Order(4)
    void cleanDatabaseAfterJPEGFileUpload() throws Exception {
        assertNotNull(idJPEG);
        String url = STR."\{ENDPOINT_URL}/delete/byId/\{idJPEG}";
        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(STR."video file with id \{idJPEG} successfully deleted."));
    }

    @Test
    @Order(5)
    void cleanDatabaseAfterPNGFileUpload() throws Exception {
        assertNotNull(idPNG);
        String url = STR."\{ENDPOINT_URL}/delete/byId/\{idPNG}";
        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(STR."video file with id \{idPNG} successfully deleted."));
    }
}