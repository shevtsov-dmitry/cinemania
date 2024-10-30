package ru.storage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.objectstorage.poster.Poster;
import ru.storage.objectstorage.poster.PosterController;
import ru.storage.objectstorage.poster.PosterService;

import java.io.File;
import java.nio.file.Files;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PosterController.class)
@ExtendWith(MockitoExtension.class)
class PosterControllerTest {

    private static final String MESSAGE_HEADER = "Message";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PosterService posterService;
    @InjectMocks
    private PosterController posterController;

    private static final File IMAGE_FILE = new File("src/test/java/ru/storage/assets/image.jpg");
    private static final String ENDPOINT_URL = "/api/v0/posters";
    private static final Random RANDOM = new Random();

    @Test
    void savePoster_201() throws Exception {
        MockMultipartFile testImageMultipartFile = new MockMultipartFile(
                "image",
                IMAGE_FILE.getName(),
                MediaType.IMAGE_JPEG_VALUE,
                Files.readAllBytes(IMAGE_FILE.toPath())
        );

        mockMvc.perform(multipart(ENDPOINT_URL + "/upload")
                        .file(testImageMultipartFile)
                        .content(MediaType.IMAGE_JPEG_VALUE)
                        .param("metadataId", String.valueOf(RANDOM.nextLong())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", isA(Long.class)));
    }

    @Test
    void savePoster_400_ParamMetadataIdAbsent() throws Exception {
        var testImageMultipartFile = new MockMultipartFile(
                "image",
                IMAGE_FILE.getName(),
                MediaType.IMAGE_JPEG_VALUE,
                Files.readAllBytes(IMAGE_FILE.toPath())
        );

        mockMvc.perform(multipart(ENDPOINT_URL + "/upload")
                        .file(testImageMultipartFile)
                        .contentType(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(MESSAGE_HEADER))
                .andExpect(header().string(MESSAGE_HEADER, not(emptyString())));
    }

    @Test
    void savePoster_400_wrongContentType() throws Exception {
        var textFile = new MockMultipartFile("image", "not image bytes".getBytes());
        mockMvc.perform(multipart(ENDPOINT_URL + "/upload")
                        .file(textFile)
                        .content(MediaType.IMAGE_JPEG_VALUE)
                        .param("metadataId", String.valueOf(RANDOM.nextLong())))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(MESSAGE_HEADER))
                .andExpect(header().string(MESSAGE_HEADER, not(emptyString())));
    }

    @Test
    void savePoster_500_CorruptedImage() throws Exception {
        when(posterService.savePoster(1L, new MockMultipartFile("image", "not image bytes".getBytes())))
                .thenThrow(new RuntimeException());// TODO appropriate exception when happens issue with processing image in service

        MockMultipartFile testImageMultipartFile = new MockMultipartFile(
                "image",
                IMAGE_FILE.getName(),
                MediaType.IMAGE_JPEG_VALUE,
                Files.readAllBytes(IMAGE_FILE.toPath())
        );

        mockMvc.perform(multipart(ENDPOINT_URL + "/upload")
                        .file(testImageMultipartFile)
                        .param("metadataId", String.valueOf(RANDOM.nextLong())))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists(MESSAGE_HEADER))
                .andExpect(header().string(MESSAGE_HEADER, not(emptyString())));
    }


    @Test
    void getImagesByMetadataId_getOne_200() throws Exception {
        when(posterService.savePoster(anyLong(), (MultipartFile) any(MultipartFile.class)))
                .thenReturn(new Poster("my poster", "image/jpeg"));
    }

//    @Test
//    void getImagesByMetadataId_500() throws Exception {
//
//    }
//
//    @Test
//    void updateExistingImage_200() throws Exception {
//
//    }
//
//    @Test
//    void updateExistingImage_400() throws Exception {
//
//    }
//
//    @Test
//    void updateExistingImage_500() throws Exception {
//
//    }
//
//    @Test
//    void deletePostersByIds_200() throws Exception {
//
//    }
//
//    @Test
//    void deletePostersByIds_500() throws Exception {
//
//    }

}
