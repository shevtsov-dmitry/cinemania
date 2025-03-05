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
import java.util.LinkedList;
import java.util.List;
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
    ContentCreator director;
    List<ContentCreator> actors;
    Poster poster;
    StandaloneVideoShow standaloneVideoShow;
    Trailer trailer;
    ContentMetadata contentMetadata;
  }

  private static final Metadata META = new Metadata();

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
              META.userPic =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
            });
  }

  @Test
  @Order(2)
  void requestForSavedImage_andTryToParseIt() throws Exception {
    assertNotNull(META.userPic);
    mockMvc
        .perform(
            get(
                serverUrl + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                META.userPic.getPersonCategory().stringValue,
                META.userPic.getId()))
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
  void saveDirector() throws Exception {
    assertNotNull(META.userPic);
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
            .userPic(META.userPic)
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
              assertEquals(META.userPic, savedCreator.getUserPic());
            })
        .andDo(
            result -> {
              String rawStringAnswer = result.getResponse().getContentAsString();
              META.director = objectMapper.readValue(rawStringAnswer, new TypeReference<>() {});
            });
  }

  @Test
  @Order(4)
  void getSavedDirector() throws Exception {
    mockMvc
        .perform(get(serverUrl + "/api/v0/metadata/content-creators/{id}", META.director.getId()))
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
  @Order(5)
  void saveActors() throws Exception {
    LinkedList<ContentCreator> actors = new LinkedList<>();

    ContentCreator actor1 =
        ContentCreator.builder()
            .name("Alen")
            .nameLatin("Alen")
            .surname("Winter")
            .surnameLatin("Winter")
            .bornPlace("USA, New York City, NY 10023")
            .heightCm(180)
            .userPic(META.userPic)
            .personCategory(PersonCategory.ACTOR)
            .age(35)
            .birthDate(LocalDate.of(1980, 5, 10))
            .build();

    ContentCreator actor2 =
        ContentCreator.builder()
            .name("John")
            .nameLatin("John")
            .surname("Doe")
            .surnameLatin("Doe")
            .bornPlace("USA, Los Angeles, CA 90012")
            .heightCm(175)
            .userPic(META.userPic)
            .personCategory(PersonCategory.ACTOR)
            .age(38)
            .birthDate(LocalDate.of(1982, 6, 15))
            .build();

    actors.add(actor1);
    actors.add(actor2);

    for (int i = 0; i < actors.size(); i++) {
      mockMvc
          .perform(
              post(serverUrl + "/api/v0/metadata/content-creators")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(actors.peek())))
          .andExpect(status().isCreated())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", notNullValue()))
          .andExpect(jsonPath("$.id", not(emptyString())))
          .andDo(
              result -> {
                ContentCreator savedActor =
                    objectMapper.readValue(
                        result.getResponse().getContentAsString(), ContentCreator.class);
                actors.pop();
                actors.addLast(savedActor);
              });
    }

    META.actors = actors;
  }

  @Test
  @Order(10)
  void saveContentMetadataForFilm() throws Exception {

    META.poster = new Poster(IMAGE_FILE.getName(), "image/jpeg", IMAGE_FILE.length());
    META.trailer = new Trailer(VIDEO_FILE.getName(), "video/mp4", VIDEO_FILE.length());
    META.standaloneVideoShow =
        new StandaloneVideoShow(VIDEO_FILE.getName(), "video/mp4", VIDEO_FILE.length());

    var filmingGroup = FilmingGroup.builder().director(META.director).actors(META.actors).build();

    var metadata =
        ContentMetadata.builder()
            .title("The Shawshank Redemption")
            .releaseDate(LocalDate.of(1994, 6, 20))
            .country(new Country("USA"))
            .mainGenre(new Genre("fiction"))
            .subGenres(List.of(new Genre("comedy"), new Genre("drama")))
            .poster(META.poster)
            .trailer(META.trailer)
            .standaloneVideoShow(META.standaloneVideoShow)
            .filmingGroup(filmingGroup)
            .description("A prison movie about a banker and his fellow inmates.")
            .age(18)
            .rating(9.3)
            .build();

    System.out.println(objectMapper.writeValueAsString(metadata));

    mockMvc
        .perform(
            post(serverUrl + "/api/v0/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(metadata)))
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
              META.contentMetadata = contentMetadata;
              META.poster = contentMetadata.getPoster();
              META.trailer = contentMetadata.getTrailer();
              META.standaloneVideoShow = contentMetadata.getStandaloneVideoShow();
            });
  }

  // ===== UPLOADS =====

  // ----- IMAGES -----

  @Test
  @Order(11)
  void uploadPoster() throws Exception {
    assertNotNull(META.director);
    var multipartFile =
        new MockMultipartFile(
            "image", IMAGE_FILE.getName(), "image/jpeg", Files.readAllBytes(IMAGE_FILE.toPath()));
    mockMvc
        .perform(
            multipart(serverUrl + "/api/v0/posters/upload")
                .file(multipartFile)
                .param("id", META.poster.getId()))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.filename", notNullValue()))
        .andExpect(jsonPath("$.size", notNullValue()))
        .andExpect(jsonPath("$.contentType", notNullValue()))
        .andDo(
            result -> {
              META.poster =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
            });
  }

  @Test
  @Order(11)
  void getPoster_thenTryToParseItToImage() throws Exception {
    assertNotNull(META.poster);
    mockMvc
        .perform(get(serverUrl + "/api/v0/posters/{id}", META.poster.getId()))
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
                .param("id", META.trailer.getId()))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.filename", notNullValue()))
        .andExpect(jsonPath("$.contentType", notNullValue()))
        .andExpect(jsonPath("$.size", notNullValue()))
        .andDo(
            result -> {
              META.trailer =
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
                .param("id", META.standaloneVideoShow.getId()))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.filename", notNullValue()))
        .andExpect(jsonPath("$.contentType", notNullValue()))
        .andExpect(jsonPath("$.size", notNullValue()))
        .andDo(
            result -> {
              META.standaloneVideoShow =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});
            });
  }

  @Order(30)
  @Test
  void deleteWholeStandaloneShowRelatedContent() throws Exception {
    assertNotNull(META.contentMetadata);
    assertThat(META.contentMetadata.getId()).isNotBlank();
    mockMvc
        .perform(delete(serverUrl + "/api/v0/metadata/{id}", META.contentMetadata.getId()))
        .andExpect(status().isNoContent());
    META.contentMetadata = null;
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
    assertNotNull(META.director);
    mockMvc
        .perform(
            delete(serverUrl + "/api/v0/metadata/content-creators/{id}", META.director.getId()))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get(serverUrl + "/api/v0/metadata/content-creators/{id}", META.director.getId()))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(1001)
  void deleteContentCreatorUserPicture() throws Exception {
    assertNotNull(META.userPic);
    mockMvc
        .perform(
            delete(
                serverUrl + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                META.userPic.getPersonCategory(),
                META.userPic.getId()))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            get(
                serverUrl + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                META.userPic.getPersonCategory(),
                META.userPic.getId()))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(1002)
  void deletePoster() throws Exception {
    assertNotNull(META.poster);
    mockMvc
        .perform(delete(serverUrl + "/api/v0/posters/{id}", META.poster.getId()))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get(serverUrl + "/api/v0/posters/{id}", META.poster.getId()))
        .andExpect(status().isNotFound());
  }
}
