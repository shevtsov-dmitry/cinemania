package ru.storage.scenary;

import static org.assertj.core.api.Assertions.assertThat;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.Data;
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
import ru.storage.content_metadata.poster.Poster;
import ru.storage.content_metadata.video.standalone.StandaloneVideoShow;
import ru.storage.content_metadata.video.trailer.Trailer;
import ru.storage.person.PersonCategory;
import ru.storage.person.content_creator.ContentCreator;
import ru.storage.person.filming_group.FilmingGroup;
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

  @Data
  private static class Metadata {
    UserPic userPic;
    ContentCreator creator;
    Poster poster;
    StandaloneVideoShow standaloneVideoShow;
    Trailer trailer;
    ContentMetadata contentMetadata;
  }

  private static Metadata meta = new Metadata();

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
        .andExpect(jsonPath("$.size", notNullValue()))
        .andExpect(jsonPath("$.personCategory", notNullValue()))
        .andDo(
            result -> {
              meta.userPic =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
            });
  }

  @Test
  @Order(2)
  void requestForSavedImage_andTryToParseIt() throws Exception {
    assertNotNull(meta.userPic);
    mockMvc
        .perform(
            get(
                serverUrl + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                meta.userPic.getPersonCategory().stringValue,
                meta.userPic.getId()))
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
    assertNotNull(meta.userPic);
    Map<String, Object> contentCreatorToBeSaved = new HashMap<>();
    // ContentCreator.builder()
    //     .id(null)
    //     .fullname("Ален")
    //     .fullnameLatin("Alen")
    //     .bornPlace("США, шт. Миссури, г. Файет")
    //     .heightCm(180)
    //     .age(35)
    //     .personCategory(PersonCategory.ACTOR)
    //     .userPic(meta.userPic)
    //     .isDead(false)
    //     .birthDate(LocalDate.of(1980, 5, 10))
    //     .deathDate(null)
    //     .build();

    contentCreatorToBeSaved.put("id", null);
    contentCreatorToBeSaved.put("fullName", "Ален");
    contentCreatorToBeSaved.put("fullLatinName", "Alen");
    contentCreatorToBeSaved.put("bornPlace", "US, шт. Миссури, г. Файет");
    contentCreatorToBeSaved.put("heightCm", 180);
    contentCreatorToBeSaved.put("age", 35);
    contentCreatorToBeSaved.put("personCategory", "ACTOR");
    contentCreatorToBeSaved.put("userPic", meta.userPic);
    contentCreatorToBeSaved.put("isDead", false);
    contentCreatorToBeSaved.put("birthDate", "10.05.1980");
    contentCreatorToBeSaved.put("deathDate", null);
    //             {
    //   "id": null,
    //   "fullName": "Ален",
    //   "fullLatinName": "Alen",
    //   "bornPlace": "US, шт. Миссури, г. Файет",
    //   "heightCm": 180,
    //   "age": 35,
    //   "personCategory": "ACTOR",
    //   "userPic": meta.userPic,
    //   "isDead": false,
    //   "birthDate": "1980-05-10",
    //   "deathDate": null
    // }

    String json = objectMapper.writeValueAsString(contentCreatorToBeSaved);
    System.out.println(json);

    mockMvc
        .perform(
            post(serverUrl + "/api/v0/metadata/content-creators")
                .contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
                .content(json))
        .andExpect(status().isCreated())
        .andDo(result -> System.out.printf("RAWWWW %s", result.getResponse().getContentAsString()))
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
              assertEquals(meta.userPic, savedCreator.getUserPic());
            })
        .andDo(
            result -> {
              String rawStringAnswer = result.getResponse().getContentAsString();
              System.out.printf("RAW STRING: %s%n", rawStringAnswer);
              meta.creator = objectMapper.readValue(rawStringAnswer, new TypeReference<>() {});
            });
  }

  @Test
  @Order(9)
  void deleteContentCreatorUserPicture() throws Exception {
    assertNotNull(meta.userPic);
    mockMvc
        .perform(
            delete(
                serverUrl + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                meta.userPic.getPersonCategory(),
                meta.userPic.getId()))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            get(
                serverUrl + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                meta.userPic.getPersonCategory(),
                meta.userPic.getId()))
        .andExpect(status().isNotFound());
  }

  // ===== UPLOADS =====

  // ----- IMAGES -----

  @Test
  @Order(10)
  void uploadPoster() throws Exception {
    assertNotNull(meta.creator);
    var multipartFile =
        new MockMultipartFile(
            "image", IMAGE_FILE.getName(), "image/jpeg", Files.readAllBytes(IMAGE_FILE.toPath()));
    mockMvc
        .perform(multipart(serverUrl + "/api/v0/posters/upload").file(multipartFile))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.filename", notNullValue()))
        .andExpect(jsonPath("$.size", notNullValue()))
        .andExpect(jsonPath("$.contentType", notNullValue()))
        .andDo(
            result -> {
              meta.poster =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
            });
  }

  @Test
  @Order(11)
  void getPoster_thenTryToParseItToImage() throws Exception {
    assertNotNull(meta.poster);
    mockMvc
        .perform(get(serverUrl + "/api/v0/posters/{id}", meta.poster.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
        .andExpect(
            result -> {
              byte[] imageRawBytes = result.getResponse().getContentAsByteArray();
              assertDoesNotThrow(() -> ImageIO.read(new ByteArrayInputStream(imageRawBytes)));
            });
  }

  @Test
  @Order(19)
  void deletePoster() throws Exception {
    assertNotNull(meta.poster);
    mockMvc
        .perform(delete(serverUrl + "/api/v0/posters/{id}", meta.poster.getId()))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get(serverUrl + "/api/v0/posters/{id}", meta.poster.getId()))
        .andExpect(status().isNotFound());
  }

  // ----- VIDEOS -----

  @Test
  @Order(20)
  void uploadStandaloneVideoShow() throws Exception {
    var multipartFile =
        new MockMultipartFile(
            "video", VIDEO_FILE.getName(), "video/mp4", Files.readAllBytes(VIDEO_FILE.toPath()));
    mockMvc
        .perform(multipart(serverUrl + "/api/v0/videos/standalone").file(multipartFile))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.filename", notNullValue()))
        .andExpect(jsonPath("$.contentType", notNullValue()))
        .andExpect(jsonPath("$.size", notNullValue()))
        .andDo(
            result -> {
              meta.standaloneVideoShow =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
            });
  }

  @Test
  @Order(21)
  void upploadTrailer() throws Exception {
    var multipartFile =
        new MockMultipartFile(
            "video", VIDEO_FILE.getName(), "video/mp4", Files.readAllBytes(VIDEO_FILE.toPath()));
    mockMvc
        .perform(multipart(serverUrl + "/api/v0/videos/trailer").file(multipartFile))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.filename", notNullValue()))
        .andExpect(jsonPath("$.contentType", notNullValue()))
        .andExpect(jsonPath("$.size", notNullValue()))
        .andDo(
            result -> {
              meta.trailer =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
            });
  }

  @Test
  @Order(22)
  void saveContentMetadataForFilm() throws Exception {

    var filmingGroup =
        FilmingGroup.builder().director(meta.creator).actors(List.of(meta.creator)).build();

    Map<String, Object> json = new HashMap<>();
    json.put("title", "The Shawshank Redemption");
    json.put("releaseDate", "20.06.1994");
    json.put("country", "USA");
    json.put("mainGenre", "fiction");
    json.put("subGenres", objectMapper.writeValueAsString(List.of("fiction", "drama")));
    json.put("description", "A prison movie about a banker and his fellow inmates.");
    json.put("age", Integer.valueOf(18));
    json.put("rating", Double.valueOf(9.3));
    json.put("poster", objectMapper.writeValueAsString(meta.poster));
    json.put("filmingGroup", objectMapper.writeValueAsString(filmingGroup));
    json.put("standalone", objectMapper.writeValueAsString(meta.standaloneVideoShow));

    mockMvc
        .perform(
            post(serverUrl + "/api/v0/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(not(emptyString()))))
        .andExpect(jsonPath("$.releaseDate", is("20.06.1994")))
        .andExpect(jsonPath("$.country", is("USA")))
        .andExpect(jsonPath("$.mainGenre", is("fiction")))
        .andExpect(jsonPath("$.subGenres", contains("fiction", "drama")))
        .andExpect(
            jsonPath("$.description", is("A prison movie about a banker and his fellow inmates.")))
        .andExpect(jsonPath("$.age", is(18)))
        .andExpect(jsonPath("$.rating", is(9.3)))
        .andExpect(jsonPath("$.poster", notNullValue()))
        .andExpect(jsonPath("$.filmingGroup", notNullValue()))
        .andExpect(jsonPath("$.filmingGroup.id", is(not(emptyString()))))
        .andExpect(jsonPath("$.trailer", notNullValue()))
        .andExpect(jsonPath("$.trailer.id", is(not(emptyString()))))
        .andExpect(jsonPath("$.standaloneVideoShow", notNullValue()))
        .andExpect(jsonPath("$.standaloneVideoShow.id", is(not(emptyString()))))
        .andDo(
            result -> {
              ContentMetadata savedMetadata =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
              meta.contentMetadata = savedMetadata;
            });
  }

  @Order(30)
  @Test
  void deleteWholeStandaloneShowRelatedContent() throws Exception {
    assertNotNull(meta.contentMetadata);
    mockMvc
        .perform(delete(serverUrl + "/api/v0/metadata/{id}", meta.contentMetadata))
        .andExpect(status().isNoContent());
    meta.contentMetadata = null;
  }

  // ----- TV SERIES -----

  // @Test
  // @Order(30)
  // void saveContentMetadataTvSeries() throws Exception {}

  // @Test
  // @Order(30)
  // void
  //
  // @Test
  // @Order(31)
  // void uploadEpisode() throws Exception {
  //     var multipartFile =
  //             new MockMultipartFile(
  //                     "video",
  //                     VIDEO_FILE.getName(),
  //                     "video/mp4",
  //                     Files.readAllBytes(VIDEO_FILE.toPath()));
  //
  //     mockMvc.perform(multipart("/api/v0/videos/episode/")
  //         .file(multipartFile)
  //     .param( "season", 1)
  //     .param( "episode", 1)
  //     .param( "contentMetadataId", ))
  // }

  @Test
  @Order(1000)
  void deleteContentCreator() throws Exception {
    assertNotNull(meta.creator);
    mockMvc
        .perform(delete(serverUrl + "/api/v0/metadata/content-creators/{id}", meta.creator.getId()))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get(serverUrl + "/api/v0/metadata/content-creators/{id}", meta.creator.getId()))
        .andExpect(status().isNotFound());
  }
}
