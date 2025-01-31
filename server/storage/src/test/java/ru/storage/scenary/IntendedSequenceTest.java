package ru.storage.scenary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.micrometer.core.annotation.TimedSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.storage.person.content_creator.ContentCreator;

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
  private String endpointUrl;

  private static ContentCreator creator;

  @Test
  @Order(1)
  void saveContentCreator() throws Exception {
    var contentCreatorToBeSaved =
        creator
            .builder()
            .id(null)
            .fullname("Ален")
            .fullnameLatin("Alen")
            .bornPlace("США, шт. Миссури, г. Файет")
            .heightCm(180)
            .age(35)
            .userPic(null)
            .isDead(false)
            .birthDate(LocalDate.of(1980, 5, 10))
            .deathDate(null)
            .build();

    mockMvc
        .perform(
            post(endpointUrl + "/api/v0/metadata/content-creators")
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
        .andExpect(jsonPath("$.userPic", is(null)))
        .andExpect(jsonPath("$.isDead").value(false))
        .andExpect(jsonPath("$.birthDate").value("2020-05-10"))
        .andExpect(jsonPath("$.deathDate", is(null)))
        .andDo(
            result -> {
              String rawStringAnswer = result.getResponse().getContentAsString();
              creator = objectMapper.readValue(rawStringAnswer, new TypeReference<>() {});
            });
  }

  @Test
  @Order(100)
  void deleteContentCreator() throws Exception {
    assertNotNull(creator);
    mockMvc
        .perform(delete(endpointUrl + "/v0/metadata/content-creators/{id}", creator.getId()))
        .andExpect(status().isNoContent());
  }
}
