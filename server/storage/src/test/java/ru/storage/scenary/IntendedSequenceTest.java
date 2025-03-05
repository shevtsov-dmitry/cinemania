package ru.storage.scenary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
import ru.storage.content_metadata.country.Country;
import ru.storage.content_metadata.genre.Genre;
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

  private static final Metadata meta = new Metadata();

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
    var contentCreatorToBeSaved =
        ContentCreator.builder()
            .id(null)
            .name("Alen")
            .surname("Winter")
            .nameLatin("Alen")
            .surnameLatin("Winter")
            .bornPlace("USA, New York City, NY 10023")
            .heightCm(180)
            .age(35)
            .personCategory(PersonCategory.ACTOR)
            .userPic(meta.userPic)
            .isDead(false)
            .birthDate(LocalDate.of(1980, 5, 10))
            .deathDate(null)
            .build();

    String json = objectMapper.writeValueAsString(contentCreatorToBeSaved);

    mockMvc
        .perform(
            post(serverUrl + "/api/v0/metadata/content-creators")
                .contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
                .content(json))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.name", is("Alen")))
        .andExpect(jsonPath("$.surname", is("Winter")))
        .andExpect(jsonPath("$.nameLatin", is("Alen")))
        .andExpect(jsonPath("$.surnameLatin", is("Winter")))
        .andExpect(jsonPath("$.bornPlace").value("USA, New York City, NY 10023"))
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
              meta.creator = objectMapper.readValue(rawStringAnswer, new TypeReference<>() {});
            });
  }

  @Test
  @Order(4)
  void getSavedCreator() throws Exception {
    mockMvc
        .perform(get(serverUrl + "/api/v0/metadata/content-creators/{id}", meta.creator.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", is(not(emptyString()))))
        .andExpect(jsonPath("$.name", is("Alen")))
        .andExpect(jsonPath("$.surname", is("Winter")))
        .andExpect(jsonPath("$.nameLatin", is("Alen")))
        .andExpect(jsonPath("$.surnameLatin", is("Winter")))
        .andExpect(jsonPath("$.bornPlace").value("USA, New York City, NY 10023"))
        .andExpect(jsonPath("$.heightCm").value(180))
        .andExpect(jsonPath("$.age").value(35))
        .andExpect(jsonPath("$.birthDate").value("10.05.1980"))
        .andExpect(jsonPath("$.deathDate", nullValue()));
  }

  @Test
  @Order(10)
  void saveContentMetadataForFilm() throws Exception {

    var filmingGroup =
        FilmingGroup.builder().director(meta.creator).actors(List.of(meta.creator)).build();

    meta.poster = new Poster(IMAGE_FILE.getName(), "image/jpeg", IMAGE_FILE.length());
    meta.trailer = new Trailer(VIDEO_FILE.getName(), "video/mp4", VIDEO_FILE.length());
    meta.standaloneVideoShow =
        new StandaloneVideoShow(VIDEO_FILE.getName(), "video/mp4", VIDEO_FILE.length());

    var json =
        ContentMetadata.builder()
            .title("The Shawshank Redemption")
            .releaseDate(LocalDate.of(1994, 6, 20))
            .country(new Country("USA"))
            .mainGenre(new Genre("fiction"))
            .subGenres(List.of(new Genre("comedy"), new Genre("drama")))
            .poster(meta.poster)
            .trailer(meta.trailer)
            .standaloneVideoShow(meta.standaloneVideoShow)
            .filmingGroup(filmingGroup)
            .description("A prison movie about a banker and his fellow inmates.")
            .age(18)
            .rating(9.3)
            .build();

    mockMvc
        .perform(
            post(serverUrl + "/api/v0/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(not(emptyString()))))
        .andExpect(jsonPath("$.releaseDate", is("20.06.1994")))
        .andExpect(
            jsonPath("$.description", is("A prison movie about a banker and his fellow inmates.")))
        .andExpect(jsonPath("$.age", is(18)))
        .andExpect(jsonPath("$.rating", is(9.3)))
        .andExpect(jsonPath("$.filmingGroup", notNullValue()))
        .andExpect(jsonPath("$.country.name", is("USA")))
        .andExpect(jsonPath("$.mainGenre.name", is("fiction")))
        .andExpect(jsonPath("$.subGenres[0].name", is("comedy")))
        .andExpect(jsonPath("$.subGenres[1].name", is("drama")))
        .andExpect(jsonPath("$.poster", notNullValue()))
        .andExpect(jsonPath("$.poster.id", notNullValue()))
        .andExpect(jsonPath("$.poster.id", not(emptyString())))
        .andExpect(jsonPath("$.trailer", notNullValue()))
        .andExpect(jsonPath("$.trailer.id", is(not(notNullValue()))))
        .andExpect(jsonPath("$.trailer.id", is(not(emptyString()))))
        .andExpect(jsonPath("$.standaloneVideoShow", notNullValue()))
        .andExpect(jsonPath("$.standaloneVideoShow.id", is(notNullValue())))
        .andExpect(jsonPath("$.standaloneVideoShow.id", is(not(emptyString()))))
        .andDo(
            result -> {
              var contentMetadata =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), ContentMetadata.class);
              meta.contentMetadata = contentMetadata;
              meta.poster = contentMetadata.getPoster();
              meta.trailer = contentMetadata.getTrailer();
              meta.standaloneVideoShow = contentMetadata.getStandaloneVideoShow();
            });
  }

  // ===== UPLOADS =====

  // ----- IMAGES -----

  @Test
  @Order(11)
  void uploadPoster() throws Exception {
    assertNotNull(meta.creator);
    var multipartFile =
        new MockMultipartFile(
            "image", IMAGE_FILE.getName(), "image/jpeg", Files.readAllBytes(IMAGE_FILE.toPath()));
    mockMvc
        .perform(
            multipart(serverUrl + "/api/v0/posters/upload")
                .file(multipartFile)
                .param("id", meta.poster.getId()))
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

  // ----- VIDEOS -----

  @Test
  @Order(20)
  void uploadTrailer() throws Exception {
    var multipartFile =
        new MockMultipartFile(
            "video", VIDEO_FILE.getName(), "video/mp4", Files.readAllBytes(VIDEO_FILE.toPath()));
    mockMvc
        .perform(
            multipart(serverUrl + "/api/v0/videos/trailer")
                .file(multipartFile)
                .param("id", meta.trailer.getId()))
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
  @Order(21)
  void uploadStandaloneVideoShow() throws Exception {
    var multipartFile =
        new MockMultipartFile(
            "video", VIDEO_FILE.getName(), "video/mp4", Files.readAllBytes(VIDEO_FILE.toPath()));
    mockMvc
        .perform(
            multipart(serverUrl + "/api/v0/videos/standalone")
                .file(multipartFile)
                .param("id", meta.standaloneVideoShow.getId()))
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

  @Order(30)
  @Test
  void deleteWholeStandaloneShowRelatedContent() throws Exception {
    assertNotNull(meta.contentMetadata);
    assertThat(meta.contentMetadata.getId()).isNotBlank();
    mockMvc
        .perform(delete(serverUrl + "/api/v0/metadata/{id}", meta.contentMetadata.getId()))
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
  // var multipartFile =
  // new MockMultipartFile(
  // "video",
  // VIDEO_FILE.getName(),
  // "video/mp4",
  // Files.readAllBytes(VIDEO_FILE.toPath()));
  //
  // mockMvc.perform(multipart("/api/v0/videos/episode/")
  // .file(multipartFile)
  // .param( "season", 1)
  // .param( "episode", 1)
  // .param( "contentMetadataId", ))
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

  @Test
  @Order(1001)
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

  @Test
  @Order(1002)
  void deletePoster() throws Exception {
    assertNotNull(meta.poster);
    mockMvc
        .perform(delete(serverUrl + "/api/v0/posters/{id}", meta.poster.getId()))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get(serverUrl + "/api/v0/posters/{id}", meta.poster.getId()))
        .andExpect(status().isNotFound());
  }
}
