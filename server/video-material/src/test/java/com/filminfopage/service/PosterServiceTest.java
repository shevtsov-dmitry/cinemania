package com.filminfopage.service;

import org.aspectj.lang.annotation.After;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback
class PosterServiceTest {

    @Autowired
    MockMvc mockMvc;
    final String endpointURL = "http://localhost:8080/film-info/posters";
    final String POSTERS_PATH = "src/test/java/com/filminfopage/assets/";


    String contentType = "multipart/form-data";
    // JPEG
    static String idJPEG;
    String nameJPEG = "sin-city-poster";
    String filenameJPEG = STR."\{nameJPEG}.jpg";
    Path pathJPEG = Paths.get(POSTERS_PATH + filenameJPEG);
    byte[] contentJPEG = Files.readAllBytes(pathJPEG);
    MockMultipartFile fileJPEG = new MockMultipartFile("file", filenameJPEG, contentType, contentJPEG);
    
    // PNG
    String namePNG = "png-image";
    String filenamePNG = STR."\{namePNG}.png";
    Path pathPNG = Paths.get(POSTERS_PATH + filenamePNG);
    byte[] contentPNG = Files.readAllBytes(pathPNG);
    MockMultipartFile filePNG = new MockMultipartFile("file", filenamePNG, contentType, contentPNG);

    PosterServiceTest() throws IOException {
    }

    @Test
    void uploadJPEGImage() throws Exception {
        String regex = "saved in database with id: [a-f0-9]+";
        String url = STR."\{endpointURL}/upload";
        mockMvc.perform(multipart(url)
                        .file(fileJPEG)
                        .param("title", nameJPEG))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(Matchers.matchesPattern(regex)))
                .andDo(result -> setId(result.getResponse().getContentAsString()));
    }

    void setId(String response) {
        String regex = "id: (\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        assertTrue(matcher.find(), STR."ID not found in response: \{response}");
        idJPEG = matcher.group(1);
    }

    @Test
    void uploadPNGImage() throws Exception {
        String url = STR."\{endpointURL}/upload";
        mockMvc.perform(multipart(url)
                        .file(filePNG)
                        .param("title", namePNG))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    @After(value = "uploadJPEGImage")
    void getById() throws Exception {
        assertNotNull(idJPEG, "Id is null.");
        String url = STR."\{endpointURL}/get/byId/\{idJPEG}";
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    @After(value = "uploadPNGImage")
    void getByTitle() throws Exception {
        String url = STR."\{endpointURL}/get/byTitle/\{namePNG}";
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    @After(value = "getById")
    void cleanDatabaseAfterJPEGFileUpload() throws Exception {
        String url = STR."\{endpointURL}/delete/byId/\{idJPEG}";
        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(STR."video file with id \{idJPEG} successfully deleted."));
    }

    @Test
    @After(value = "getByTitle")
    void cleanDatabaseAfterPNGFileUpload() throws Exception {
        String url = STR."\{endpointURL}/delete/byTitle/\{namePNG}";
        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(STR."video file with title \{namePNG} successfully deleted."));

    }
}