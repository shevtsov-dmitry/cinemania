package ru.storage.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.storage.content.poster.PosterMetadata;
import ru.storage.content.video.VideoMetadata;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContentController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContentControllerTest {

    public static final String API_PATH = "/api/v0/metadata";
    private static VideoInfoParts testMetadata;

    @MockBean
    private ContentService contentService;

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
                randomData.nextObject(ContentDetails.class, "video", "poster", "createdAt"),
                randomData.nextObject(VideoMetadata.class, "contentMetadata"),
                randomData.nextObject(PosterMetadata.class, "contentMetadata")
        );
        testMetadata.videoMetadata().setContentType("video/mp4");
        testMetadata.posterMetadata().setContentType("image/jpeg");
    }

    @Test
    void saveFormData_ok() throws Exception {
        when(contentService.saveMetadata(testMetadata)).thenReturn(any(ContentDetails.class));
        String json = objectMapper.writeValueAsString(testMetadata);
        mockMvc.perform(post("/api/v0/metadata")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void saveFormData_badRequest() throws Exception {
        when(contentService.saveMetadata(testMetadata)).thenThrow(IllegalArgumentException.class);
        String json = objectMapper.writeValueAsString(testMetadata);
        mockMvc.perform(post(API_PATH)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}