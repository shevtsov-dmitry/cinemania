package ru.storage.scenary;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import javax.imageio.ImageIO;

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
import ru.storage.content_metadata.ContentMetadata;
import ru.storage.content_metadata.VideoInfoParts;
import ru.storage.content_metadata.poster.Poster;
import ru.storage.content_metadata.video.Video;

/**
 * Tests behavior:
 *
 * <ol>
 *   <li>receive json with 3 objects
 *   <li>save their metadata into local db
 *   <li>save poster into S3
 *   <li>save video into S3 in HLS format
 *   <li>receive saved poster
 *   <li>stream saved video chunks
 *   <li>finally automatic clean up
 * </ol>
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UploadFlowTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private static final File IMAGE_FILE = new File("src/test/java/ru/storage/assets/image.jpg");
  private static final File VIDEO_FILE =
      new File("src/test/java/ru/storage/assets/video_sample.mp4");
  private static ContentMetadata savedContentMetadata;

  @Value("${server.url}")
  private String endpointUrl;

  // ------------- METADATA -------------

  @Test
  @Order(1)
  void saveMetadata() throws Exception {
    var contentDetails = new ContentMetadata();
    contentDetails.setTitle("test title");
    contentDetails.setReleaseDate(LocalDate.of(2023, 1, 31));
    contentDetails.setCountry("Romania");
    contentDetails.setMainGenre("Drama");
    contentDetails.setSubGenres(List.of("Family", "Comedy"));
    contentDetails.setAge(6);
    contentDetails.setRating(9.5D);
    var videoInfoParts =
        new VideoInfoParts(
            contentDetails,
            new Video(null, VIDEO_FILE.getName(), "video/mp4"),
            new Poster(null, IMAGE_FILE.getName(), "image/jpeg"));

    String json = objectMapper.writeValueAsString(videoInfoParts);
    mockMvc
        .perform(
            post(endpointUrl + "/api/v0/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(
            answer -> {
              String resString = answer.getResponse().getContentAsString();
              savedContentMetadata = objectMapper.readValue(resString, ContentMetadata.class);
            });
  }

  @Test
  @Order(2)
  void getSavedMetadata() throws Exception {
    assertNotNull(savedContentMetadata);
    mockMvc
        .perform(get(endpointUrl + "/api/v0/metadata/recent/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(
            result -> {
              List<ContentMetadata> recentMetadataList =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(),
                      new TypeReference<List<ContentMetadata>>() {});
              var saved = recentMetadataList.getFirst();
              assertFalse(saved.getTitle().isBlank());
              assertNotNull(saved.getPoster());
              assertNotNull(saved.getVideo());
              savedContentMetadata = saved;
            });
  }

  // ------------- POSTER -------------

  @Test
  @Order(3)
  void savePoster() throws Exception {
    final var posterId = savedContentMetadata.getPoster().getId();
    final var imageMultipartFile =
        new MockMultipartFile(
            "image", posterId, "image/jpeg", Files.readAllBytes(IMAGE_FILE.toPath()));
    assertNotNull(savedContentMetadata.getPoster());

    mockMvc
        .perform(
            multipart(endpointUrl + "/api/v0/posters/upload")
                .file(imageMultipartFile)
                .param("id", posterId)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(4)
  void getSavedPoster() throws Exception {
    final var posterId = savedContentMetadata.getPoster().getId();
    mockMvc
        .perform(get(endpointUrl + "/api/v0/posters/" + posterId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
        .andExpect(result -> {
             byte[] rawBytes = result.getResponse().getContentAsByteArray();
              assertThat(rawBytes).isNotNull().isNotEmpty();
              assertDoesNotThrow(() -> ImageIO.read(new ByteArrayInputStream(rawBytes)));
        });
  }

  // ------------- VIDEO -------------

  @Test
  @Order(11)
  void saveVideo() throws Exception {
    final var videoId = savedContentMetadata.getVideo().getId();
    final var videoMultipartFile =
        new MockMultipartFile(
            "video", videoId, "video/mp4", Files.readAllBytes(VIDEO_FILE.toPath()));
    assertNotNull(savedContentMetadata.getPoster());

    mockMvc
        .perform(
            multipart(endpointUrl + "/api/v0/videos/upload")
                .file(videoMultipartFile)
                .param("id", videoId))
        .andExpect(status().isCreated());
  }

  // ------------- CLEAN UP -------------
  @Test
  @Order(100)
  void deleteMetadata() throws Exception {
    assertFalse(savedContentMetadata.getId().isBlank());
    mockMvc
        .perform(delete(endpointUrl + "/api/v0/metadata/" + savedContentMetadata.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(101)
  void deletePoster() throws Exception {
    assertFalse(savedContentMetadata.getId().isBlank());
    mockMvc
        .perform(
            delete(
                endpointUrl + "/api/v0/posters/" + savedContentMetadata.getPoster().getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(102)
  void deleteVideo() throws Exception {
    assertFalse(savedContentMetadata.getId().isBlank());
    mockMvc
        .perform(
            delete(
                endpointUrl + "/api/v0/videos/" + savedContentMetadata.getVideo().getId()))
        .andExpect(status().isNoContent());
  }
}
