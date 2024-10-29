package ru.storage.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.objectstorage.poster.Poster;
import ru.storage.objectstorage.poster.PosterController;
import ru.storage.objectstorage.poster.PosterService;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PosterControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private PosterService service;
    @InjectMocks
    private PosterController controller;

    @Value("${SERVER_URL: }")
    private static String SERVER_URL;
    private static String ENDPOINT_URL;

    @BeforeAll
    static void setUp() {
        assertFalse(SERVER_URL.isBlank(), "Необходимо указать переменную среды SERVER_URL.");
        ENDPOINT_URL = SERVER_URL + "/api/v1/posters";
    }

    // Static ID to be used across tests
    private static Long savedMetadataId;
    private static final File imageFile = new File("src/test/java/ru/storage/assets/image.jpg");
    private static String contentMetadataIds = savedMetadataId.toString();

    private static final MockMultipartFile formDataImage;

    static {
        try {
            formDataImage = new MockMultipartFile(
                    "image", "image.jpg", "image/jpeg", Files.readAllBytes(imageFile.toPath()));
        } catch (IOException e) {
            Assert.isTrue(imageFile.exists(), "Ошибка при чтении тестового файла изображения постера.");
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

//    @Test
//    void testImageIsAvailable_Success() {
//        Assertions.assertTrue(resource.exists());
//    }

    // Ordered Successful Tests

    @Test
    @Order(1)
    void testSavePoster_Success() throws Exception {
        Long metadataId = 100L; // You can set this to any value you prefer

        Poster poster = new Poster();
        poster.setId(metadataId);
        when(service.savePoster(eq(metadataId), any(MultipartFile.class))).thenReturn(poster);

        mockMvc.perform(multipart(ENDPOINT_URL + "/upload")
                        .file(formDataImage)
                        .param("metadataId", metadataId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(metadataId));

        // Save the metadataId for use in subsequent tests
        savedMetadataId = metadataId;
    }

    @Test
    @Order(2)
    void testGetImagesByContentMetadataId_Success() throws Exception {
        assertThat(savedMetadataId).isNotNull();

        when(service.getImagesByContentMetadataId(contentMetadataIds))
                .thenReturn();

        mockMvc.perform(get(ENDPOINT_URL + "/images/{contentMetadataIds}", contentMetadataIds))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void testUpdateExistingImage_Success() throws Exception {
        Long metadataId = savedMetadataId;
        MockMultipartFile formDataImage = new MockMultipartFile(
                "image", "updated_image.jpg", "image/jpeg", "updated image content".getBytes());

        doNothing().when(service).updateExistingImage(eq(metadataId), any(MultipartFile.class));

        mockMvc.perform(multipart(ENDPOINT_URL + "/change")
                        .file(formDataImage)
                        .param("metadataId", metadataId.toString())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(header().exists("message"))
                .andExpect(header().string("message", Matchers.not(Matchers.emptyString())));
    }

    @Test
    @Order(4)
    void testDeletePosterById_Success() throws Exception {
        doNothing().when(service).deleteByIds(contentMetadataIds);

        mockMvc.perform(delete(ENDPOINT_URL + "/ids/{contentMetadataIds}", contentMetadataIds))
                .andExpect(status().isOk())
                .andExpect(header().exists("message"))
                .andExpect(header().string("message", Matchers.not(Matchers.emptyString())));
    }

    // Exception Tests (Not Ordered)

    @Test
    void testSavePoster_InvalidContentType() throws Exception {
        Long metadataId = 200L;
        MockMultipartFile textInsteadOfImage = new MockMultipartFile(
                "image", "file.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(multipart(ENDPOINT_URL + "/upload")
                        .file(textInsteadOfImage)
                        .param("metadataId", metadataId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("message"))
                .andExpect(header().string("message", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void testSavePoster_Exception() throws Exception {
        Long metadataId = 300L;

        when(service.savePoster(eq(metadataId), any(MultipartFile.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(multipart(ENDPOINT_URL + "/upload")
                        .file(formDataImage)
                        .param("metadataId", metadataId.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("message"))
                .andExpect(header().string("message", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void testGetImagesByContentMetadataId_Exception() throws Exception {

        when(service.getImagesByContentMetadataId(contentMetadataIds))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get(ENDPOINT_URL + "/images/{contentMetadataIds}", contentMetadataIds))
                .andExpect(status().isInternalServerError())
                .andExpect(header().string("message", "Ошибка при получении постеров."));
    }

    @Test
    void testUpdateExistingImage_InvalidContentType() throws Exception {
        Long metadataId = savedMetadataId != null ? savedMetadataId : 700L;

        mockMvc.perform(multipart(ENDPOINT_URL + "/change")
                        .file(formDataImage)
                        .param("metadataId", metadataId.toString())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("message"))
                .andExpect(header().string("message", Matchers.not(Matchers.emptyString())));
    }

    @Test
    void testUpdateExistingImage_Exception() throws Exception {
        Long metadataId = savedMetadataId != null ? savedMetadataId : 800L;

        doThrow(new RuntimeException("Database error"))
                .when(service).updateExistingImage(eq(metadataId), any(MultipartFile.class));

        mockMvc.perform(multipart(ENDPOINT_URL + "/change")
                        .file(formDataImage)
                        .param("metadataId", metadataId.toString())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isInternalServerError())
                .andExpect(header().string("message", "Неудалось заменить существующий постер."));
    }

    @Test
    void testDeletePostersByIds_InternalServerError() throws Exception {
        String contentMetadataIds = savedMetadataId != null ? savedMetadataId.toString() : "900,1000";

        doThrow(new RuntimeException("Database error")).when(service).deleteByIds("123,44,201");

        mockMvc.perform(delete(ENDPOINT_URL + "/ids/{contentMetadataIds}", contentMetadataIds))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("message"))
                .andExpect(header().string("message", Matchers.not(Matchers.emptyString())));
    }
}
