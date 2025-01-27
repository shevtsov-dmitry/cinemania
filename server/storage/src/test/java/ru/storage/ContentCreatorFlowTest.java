package ru.storage;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
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
import ru.storage.content.ContentDetails;
import ru.storage.content_creators.ContentCreator;
import ru.storage.person.PersonCategory;
import ru.storage.userpic.UserPic;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContentCreatorFlowTest {
  @Value("${server.url}")
  private String serverUrl;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  private static final File IMAGE_FILE = new File("src/test/java/ru/storage/assets/image.jpg");

  private static UserPic savedUserPicMetadata;
  private static ContentCreator savedContentCreator;

  @Test
  @Order(1)
  void savePicture() throws Exception {

    var multipartFile =
        new MockMultipartFile(
            "image",
            "image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            Files.readAllBytes(IMAGE_FILE.toPath()));

    mockMvc
        .perform(
            multipart(serverUrl + "/api/v0/content-creators/user-pics/upload")
                .file(multipartFile)
                .param("personCategory", PersonCategory.ACTOR.stringValue))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(not(emptyString()))))
        .andExpect(jsonPath("$.contentType", is(MediaType.IMAGE_JPEG_VALUE)))
        .andExpect(jsonPath("$.filename", is("image.jpg")))
        .andExpect(jsonPath("$.personCategory", is(PersonCategory.ACTOR.stringValue)))
        .andDo(
            result -> {
              savedUserPicMetadata =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
            });
  }

  @Test
  @Order(2)
  void saveMetadata() throws Exception {

    var contentCreator =
        ContentCreator.builder()
            .fullname("Джон вик")
            .fullnameEng("John Ouieek")
            .bornPlace("США, Мэриленд")
            .heightMeters(1.77D)
            .isDead(true)
            .birthDate(LocalDate.of(1975, 10, 24))
            .deathDate(LocalDate.of(2015, 10, 24))
            .userPic(savedUserPicMetadata)
            .filmsParticipated(List.of(new ContentDetails()))
            .build();

    mockMvc
        .perform(
            post(serverUrl + "/api/v0/content-creators")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contentCreator)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.fullname", is("Джон вик")))
        .andExpect(jsonPath("$.fullnameEng", is("John Ouieek")))
        .andExpect(jsonPath("$.bornPlace", is("США, Мэриленд")))
        .andExpect(jsonPath("$.isDead", is(true)))
        .andExpect(jsonPath("$.birthDate", is("24.10.1975")))
        .andExpect(jsonPath("$.deathDate", is("24.10.2015")))
        .andExpect(jsonPath("$.filmsParticipated", iterableWithSize(1)))
        .andExpect(
            result -> {
              ContentCreator creator =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
              assertEquals(savedUserPicMetadata, creator.getUserPic());
            })
        .andDo(
            result -> {
              savedContentCreator =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
            });
  }

  @Order(3)
  @Test
  void getSavedPic() throws Exception {
    mockMvc
        .perform(
            get(
                serverUrl
                    + "/api/v0/content-creators/user-pics/%s/%s"
                        .formatted(
                            savedContentCreator.getUserPic().getPersonCategory(),
                            savedContentCreator.getUserPic().getId())))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
        .andExpect(
            result -> {
              byte[] rawBytes = result.getResponse().getContentAsByteArray();
              assertThat(rawBytes).isNotNull().isNotEmpty();
              assertDoesNotThrow(() -> ImageIO.read(new ByteArrayInputStream(rawBytes)));
            });
  }

  /**
   * @apiNote This endpoint function removes creator metadata, image metadata from db and image from
   *     s3, because there is no point to leave image if creator does not exists.
   */
  @Test
  @Order(101)
  void deleteMetadata() throws Exception {
    assertThat(savedContentCreator.getId()).isNotNull().isNotBlank();
    String url = serverUrl + "/api/v0/content-creators/" + savedContentCreator.getId();
    mockMvc.perform(delete(url)).andExpect(status().isNoContent());
  }
}
