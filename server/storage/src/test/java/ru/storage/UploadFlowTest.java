package ru.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.storage.content.ContentDetails;
import ru.storage.content.VideoInfoParts;
import ru.storage.content.poster.PosterMetadata;
import ru.storage.content.video.VideoMetadata;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests behavior:
 * <ol>
 * <li>receive json with 3 objects</li>
 * <li>save their metadata into local db</li>
 * <li>save poster into S3</li>
 * <li>save video into S3 in HLS format</li>
 * <li>receive saved poster</li>
 * <li>stream saved video chunks</li>
 *
 * <li>finally automatic clean up</li>
 * </ol>
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UploadFlowTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final File IMAGE_FILE = new File("src/test/java/ru/storage/assets/image.jpg");
    private static final File VIDEO_FILE = new File("src/test/java/ru/storage/assets/video_sample.mp4");
    private static ContentDetails savedContentDetails;
    @Value("${server.url}")
    private String endpointUrl;

    // ------------- METADATA -------------

    @Test
    @Order(1)
    void saveMetadata() throws Exception {
        var contentDetails = new ContentDetails();
        contentDetails.setTitle("test title");
        contentDetails.setReleaseDate(new Date());
        contentDetails.setCountry("Romania");
        contentDetails.setMainGenre("Drama");
        contentDetails.setSubGenres(List.of("Family", "Comedy"));
        contentDetails.setAge(6);
        contentDetails.setRating(9.5D);
        var videoInfoParts = new VideoInfoParts(
                contentDetails,
                new VideoMetadata(null,
                        VIDEO_FILE.getName(),
                        "video/mp4"),
                new PosterMetadata(null,
                        IMAGE_FILE.getName(),
                        "image/jpeg")
        );

        String json = objectMapper.writeValueAsString(videoInfoParts);
        mockMvc.perform(post(endpointUrl + "/api/v0/metadata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(answer -> {
                    String resString = answer.getResponse().getContentAsString();
                    savedContentDetails = objectMapper.readValue(resString, ContentDetails.class);
                });
    }

    @Test
    @Order(2)
    void getSavedMetadata() throws Exception {
        assertNotNull(savedContentDetails);
        mockMvc.perform(get(endpointUrl + "/api/v0/metadata/recent/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                        List<ContentDetails> recentMetadataList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ContentDetails>>() {  });
                        var saved = recentMetadataList.getFirst();
                        assertFalse(saved.getTitle().isBlank());
                        assertNotNull(saved.getPosterMetadata());
                        assertNotNull(saved.getVideoMetadata());
                        savedContentDetails = saved;
                });
    }

    // ------------- POSTER -------------

    @Test
    @Order(3)
    void savePoster() throws Exception {
        final var posterId = savedContentDetails.getPosterMetadata().getId();
        final var imageMultipartFile =
                new MockMultipartFile("image",
                        posterId,
                        "image/jpeg",
                        Files.readAllBytes(IMAGE_FILE.toPath()));
        assertNotNull(savedContentDetails.getPosterMetadata());

        mockMvc.perform(multipart(endpointUrl + "/api/v0/posters/upload")
                .file(imageMultipartFile)
                .param("id", savedContentDetails.getPosterMetadata().getId())
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
    }

    @Test
    @Order(4)
    void getSavedPoster() throws Exception {
        final var posterId = savedContentDetails.getPosterMetadata().getId();
            mockMvc.perform(get(endpointUrl + "/api/v0/posters/" + posterId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    List<String> postersList = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
                    byte[] receivedImage = Base64.getDecoder().decode(postersList.getFirst());
                    System.out.printf("%s and %s LOVELY", receivedImage.length, IMAGE_FILE.length());
                    assertTrue(receivedImage.length > 0);
                });
    }

    // ------------- VIDEO -------------


    // ------------- CLEAN UP -------------
    @Test
    @Order(100)
    void deleteMetadata() throws Exception {
        assertFalse(savedContentDetails.getId().isBlank());
        mockMvc.perform(delete(endpointUrl + "/api/v0/metadata/" + savedContentDetails.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(101)
    void deletePoster() throws Exception {
        assertFalse(savedContentDetails.getId().isBlank());
        mockMvc.perform(delete(endpointUrl + "/api/v0/posters/" + savedContentDetails.getPosterMetadata().getId()))
                .andExpect(status().isNoContent());
    }
}
