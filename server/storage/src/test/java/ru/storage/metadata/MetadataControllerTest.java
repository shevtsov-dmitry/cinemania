package ru.storage.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.video.Video;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetadataController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class MetadataControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final EnhancedRandom randomDataGenerator = EnhancedRandomBuilder.aNewEnhancedRandom();

    private static Content content;

    @BeforeAll
    static void setUp() {
        content = randomDataGenerator.nextObject(Content.class);
        content.setId(null);
        content.setPoster(randomDataGenerator.nextObject(Poster.class));
        content.setVideo(randomDataGenerator.nextObject(Video.class));
        content.setCreatedAt(null);
    }

    @Test
    @Order(1)
    void saveMetadata_ok() throws Exception {
        String json = objectMapper.writeValueAsString(content);

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(content));
    }

    @Test
    @Order(2)
    void getMetadataById() throws Exception {
    }
}