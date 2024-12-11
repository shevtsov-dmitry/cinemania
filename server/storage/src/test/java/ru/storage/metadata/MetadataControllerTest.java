package ru.storage.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.video.Video;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetadataController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MetadataControllerTest {

    public static final String API_PATH = "/api/v0/metadata";
    private static VideoInfoParts testMetadata;

    @MockBean
    private MetadataService metadataService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        EnhancedRandom randomData = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .excludeField(field -> field.getName().equals("id"))
                .collectionSizeRange(1, 5)
                .build();

        testMetadata = new VideoInfoParts(
                randomData.nextObject(ContentMetadata.class, "video", "poster", "createdAt"),
                randomData.nextObject(Video.class, "contentMetadata"),
                randomData.nextObject(Poster.class, "contentMetadata")
        );
        testMetadata.video().setContentType("video/mp4");
        testMetadata.poster().setContentType("image/jpeg");
    }

    @Test
    void saveFormData_ok() throws Exception {
        when(metadataService.saveMetadata(testMetadata)).thenReturn(any(ContentMetadata.class));
        String json = objectMapper.writeValueAsString(testMetadata);
        mockMvc.perform(post("/api/v0/metadata")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void saveFormData_badRequest() throws Exception {
        when(metadataService.saveMetadata(testMetadata)).thenThrow(IllegalArgumentException.class);
        String json = objectMapper.writeValueAsString(testMetadata);
        mockMvc.perform(post(API_PATH)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}