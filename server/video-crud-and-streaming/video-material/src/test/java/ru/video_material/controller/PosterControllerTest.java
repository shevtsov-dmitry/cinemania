package ru.video_material.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.video_material.COMMON.ASSETS_PATH;
import static ru.video_material.COMMON.HOST;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PosterControllerTest {

    @Autowired
    MockMvc mockMvc;

    @LocalServerPort
    private int PORT;
    private final String ENDPOINT_URL = UriComponentsBuilder.fromHttpUrl(HOST).port(PORT).path("posters").toUriString();
    private final String CONTENT_TYPE = "multipart/form-data";

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

    @Test
    @Order(1)
    public void uploadJPEGImage() throws Exception {
        String filenameJPEG = "sin-city-poster.jpg";
        Path pathJPEG = Paths.get(ASSETS_PATH + filenameJPEG);
        byte[] contentJPEG = Files.readAllBytes(pathJPEG);
        MockMultipartFile fileJPEG = new MockMultipartFile("file", filenameJPEG, CONTENT_TYPE, contentJPEG);

        String url = "%s/upload".formatted(ENDPOINT_URL);
        mockMvc.perform(multipart(url)
                .file(fileJPEG))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.emptyString())));
    }



}
