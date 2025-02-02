package ru.storage.scenary;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.TimedSet;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
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
import ru.storage.person.PersonCategory;
import ru.storage.person.content_creator.ContentCreator;
import ru.storage.person.userpic.UserPic;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntendedSequenceTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private static final File IMAGE_FILE = new File("src/test/java/ru/storage/assets/image.jpg");
  private static final File VIDEO_FILE =
      new File("src/test/java/ru/storage/assets/video_sample.mp4");

  @Value("${server.url}")
  private String serverUrl;

  private static UserPic userPicMetadata;
  private static ContentCreator creator;

  // ===== ADD CONTENT CREATOR =====

  @Test
  @Order(1)
  void saveContentCreatorUserPicture() throws Exception {
    var multipartFile =
        new MockMultipartFile(
            "image", IMAGE_FILE.getName(), "image/jpeg", Files.readAllBytes(IMAGE_FILE.toPath()));
    mockMvc
        .perform(
            multipart(serverUrl + "/api/v0/metadata/content-creators/user-pics/upload")
                .file(multipartFile)
                .param("personCategory", PersonCategory.ACTOR.stringValue))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.contentType", notNullValue()))
        .andExpect(jsonPath("$.filename", notNullValue()))
        .andExpect(jsonPath("$.personCategory", notNullValue()))
        .andDo(
            result -> {
              userPicMetadata =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
            });
  }

  @Test
  @Order(2)
  void requestForSavedImage_andTryToParseIt() throws Exception {
    assertNotNull(userPicMetadata);
    mockMvc
        .perform(
            get(
                serverUrl + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                userPicMetadata.getPersonCategory().stringValue,
                userPicMetadata.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
        .andExpect(
            result -> {
              byte[] savedImageBytes = result.getResponse().getContentAsByteArray();
              assertThat(savedImageBytes).isNotEmpty();
              assertDoesNotThrow(() -> ImageIO.read(new ByteArrayInputStream(savedImageBytes)));
            });
  }

  @Test
  @Order(3)
  void saveContentCreator() throws Exception {
    assertNotNull(userPicMetadata);
    var contentCreatorToBeSaved =
        ContentCreator.builder()
            .id(null)
            .fullname("Ален")
            .fullnameLatin("Alen")
            .bornPlace("США, шт. Миссури, г. Файет")
            .heightCm(180)
            .age(35)
            .personCategory(PersonCategory.ACTOR)
            .userPic(userPicMetadata)
            .isDead(false)
            .birthDate(LocalDate.of(1980, 5, 10))
            .deathDate(null)
            .build();

    mockMvc
        .perform(
            post(serverUrl + "/api/v0/metadata/content-creators")
                .header("Content-Type", "application/json")
                .content(objectMapper.writeValueAsString(contentCreatorToBeSaved)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.fullname").value("Ален"))
        .andExpect(jsonPath("$.fullnameLatin").value("Alen"))
        .andExpect(jsonPath("$.bornPlace").value("США, шт. Миссури, г. Файет"))
        .andExpect(jsonPath("$.heightCm").value(180))
        .andExpect(jsonPath("$.age").value(35))
        .andExpect(jsonPath("$.birthDate").value("10.05.1980"))
        .andExpect(jsonPath("$.deathDate", nullValue()))
        .andExpect(
            result -> {
              ContentCreator savedCreator =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
              assertEquals(userPicMetadata, savedCreator.getUserPic());
            })
        .andDo(
            result -> {
              String rawStringAnswer = result.getResponse().getContentAsString();
              creator = objectMapper.readValue(rawStringAnswer, new TypeReference<>() {});
            });
  }
  
 // ===== UPLOAD POSTER ===== 

  @Test 
  @Order(10)
  void uploadPoster() throws Exception {
    assertNotNull(creator);
    String posterPath = "src/test/resources/poster.jpg";
    File file = new File(posterPath);
  }


 // ===== UPLOAD STANDALONE VIDEO SHOW TO S3 CONTENT ===== 
  

  // ===== CLEAN UP =====

  @Test
  @Order(100)
  void deleteContentCreatorUserPicture() throws Exception {
    assertNotNull(userPicMetadata);
    mockMvc
        .perform(
            delete(
                serverUrl + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                userPicMetadata.getPersonCategory(),
                userPicMetadata.getId()))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            get(
                serverUrl + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                userPicMetadata.getPersonCategory(),
                userPicMetadata.getId()))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(101)
  void deleteContentCreator() throws Exception {
    assertNotNull(creator);
    mockMvc
        .perform(delete(serverUrl + "/api/v0/metadata/content-creators/{id}", creator.getId()))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get(serverUrl + "/api/v0/metadata/content-creators/{id}", creator.getId()))
        .andExpect(status().isNotFound());
  }
}
