package ru.storage.scenary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import ru.storage.content_metadata.poster.Poster;
import ru.storage.content_metadata.video.StandaloneVideoShow;
import ru.storage.content_metadata.video.Trailer;
import ru.storage.person.PersonCategory;
import ru.storage.person.content_creator.ContentCreator;
import ru.storage.person.userpic.UserPic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

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
    }

    private static Metadata meta = new Metadata();

    // ===== ADD CONTENT CREATOR =====

    @Test
    @Order(1)
    void saveContentCreatorUserPicture() throws Exception {
        var multipartFile =
                new MockMultipartFile(
                        "image",
                        IMAGE_FILE.getName(),
                        "image/jpeg",
                        Files.readAllBytes(IMAGE_FILE.toPath()));
        mockMvc.perform(
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
                                            result.getResponse().getContentAsString(),
                                            new TypeReference<>() {});
                        });
    }

    @Test
    @Order(2)
    void requestForSavedImage_andTryToParseIt() throws Exception {
        assertNotNull(meta.userPic);
        mockMvc.perform(
                        get(
                                serverUrl
                                        + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                                meta.userPic.getPersonCategory().stringValue,
                                meta.userPic.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(
                        result -> {
                            byte[] savedImageBytes = result.getResponse().getContentAsByteArray();
                            assertThat(savedImageBytes).isNotEmpty();
                            assertDoesNotThrow(
                                    () -> ImageIO.read(new ByteArrayInputStream(savedImageBytes)));
                        });
    }

    @Test
    @Order(3)
    void saveContentCreator() throws Exception {
        assertNotNull(meta.userPic);
        var contentCreatorToBeSaved =
                ContentCreator.builder()
                        .id(null)
                        .fullname("Ален")
                        .fullnameLatin("Alen")
                        .bornPlace("США, шт. Миссури, г. Файет")
                        .heightCm(180)
                        .age(35)
                        .personCategory(PersonCategory.ACTOR)
                        .userPic(meta.userPic)
                        .isDead(false)
                        .birthDate(LocalDate.of(1980, 5, 10))
                        .deathDate(null)
                        .build();

        mockMvc.perform(
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
                                            result.getResponse().getContentAsString(),
                                            new TypeReference<>() {});
                            assertEquals(meta.userPic, savedCreator.getUserPic());
                        })
                .andDo(
                        result -> {
                            String rawStringAnswer = result.getResponse().getContentAsString();
                            meta.creator =
                                    objectMapper.readValue(
                                            rawStringAnswer, new TypeReference<>() {});
                        });
    }

    @Test
    @Order(9)
    void deleteContentCreatorUserPicture() throws Exception {
        assertNotNull(meta.userPic);
        mockMvc.perform(
                        delete(
                                serverUrl
                                        + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
                                meta.userPic.getPersonCategory(),
                                meta.userPic.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(
                                serverUrl
                                        + "/api/v0/metadata/content-creators/user-pics/{personCategory}/{id}",
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
                        "image",
                        IMAGE_FILE.getName(),
                        "image/jpeg",
                        Files.readAllBytes(IMAGE_FILE.toPath()));
        mockMvc.perform(multipart(serverUrl + "/api/v0/posters/upload").file(multipartFile))
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
                                            result.getResponse().getContentAsString(),
                                            new TypeReference<>() {});
                        });
    }

    @Test
    @Order(11)
    void getPoster_thenTryToParseItToImage() throws Exception {
        assertNotNull(meta.poster);
        mockMvc.perform(get(serverUrl + "/api/v0/posters/{id}", meta.poster.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(
                        result -> {
                            byte[] imageRawBytes = result.getResponse().getContentAsByteArray();
                            assertDoesNotThrow(
                                    () -> ImageIO.read(new ByteArrayInputStream(imageRawBytes)));
                        });
    }

    @Test
    @Order(19)
    void deletePoster() throws Exception {
        assertNotNull(meta.poster);
        mockMvc.perform(delete(serverUrl + "/api/v0/posters/{id}", meta.poster.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(serverUrl + "/api/v0/posters/{id}", meta.poster.getId()))
                .andExpect(status().isNotFound());
    }

    // ----- VIDEOS -----

    @Test
    @Order(20)
    void uploadStandaloneVideoShow() throws Exception {
        var multipartFile =
                new MockMultipartFile(
                        "video",
                        VIDEO_FILE.getName(),
                        "video/mp4",
                        Files.readAllBytes(VIDEO_FILE.toPath()));
        mockMvc.perform(multipart(serverUrl + "/api/v0/videos/standalone").file(multipartFile))
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
                                            result.getResponse().getContentAsString(),
                                            new TypeReference<>() {});
                        });
    }

    @Test
    @Order(21)
    void upploadTrailer() throws Exception {
        var multipartFile =
                new MockMultipartFile(
                        "video",
                        VIDEO_FILE.getName(),
                        "video/mp4",
                        Files.readAllBytes(VIDEO_FILE.toPath()));
        mockMvc.perform(multipart(serverUrl + "/api/v0/videos/trailer").file(multipartFile))
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
                                            result.getResponse().getContentAsString(),
                                            new TypeReference<>() {});
                        });
    }

    @Test
    @Order(22)
    void saveContentMetadataForFilm() throws Exception {

  // id: string;
  // title: string;
  // releaseDate: string;
  // country: string;
  // mainGenre: Genre;
  // subGenres: Genre[];
  // description: string;
  // age: number;
  // rating: number;
  // poster: Poster;
  // filmingGroup: FilmingGroup;
  // singleVideoShow: StandaloneVideoShow;
  // trailer: Trailer;
  // standalone: StandaloneVideoShow;
  // tvSeries: TvSeries;
        Map<String, ?> json = new HashMap<>();
        json.put("title", "The Shawshank Redemption");
        json.put("releaseDate", "1994-06-20");
        json.put("country", "USA");
        json.put("mainGenre", "fiction");
        json.put("subGenres", "[\"fiction\", \"drama\"]");
        json.put("description", "A prison movie about a banker and his fellow inmates.");
        json.put("age", Integer.valueOf(18));
        json.put("rating", Double.valueOf(9.3));
        json.put("poster", objectMapper.writeValueAsString(meta.poster));
        json.put("filmingGroup", objectMapper.writeValueAsString(meta.filmingGroup));
        json.put("singleVideoShow", objectMapper.writeValueAsString(meta.singleVideoShow));
        json.put("standalone", objectMapper.writeValueAsString(meta.standalone));
    }

    @Test
    @Order(29)
    void deleteStandaloneVideoShow() throws Exception {
        assertNotNull(meta.standaloneVideoShow);
        mockMvc.perform(
                        delete(
                                serverUrl + "/api/v0/videos/standalone/{id}",
                                meta.standaloneVideoShow.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(29)
    void deleteTrailer() throws Exception {
        assertNotNull(meta.trailer);
        mockMvc.perform(delete(serverUrl + "/api/v0/videos/trailer/{id}", meta.trailer.getId()))
                .andExpect(status().isNoContent());
    }

    // ----- TV SERIES -----

    @Test
    @Order(30)
    void saveContentMetadataTvSeries() throws Exception {

        //
    }

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
        mockMvc.perform(
                        delete(
                                serverUrl + "/api/v0/metadata/content-creators/{id}",
                                meta.creator.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(
                                serverUrl + "/api/v0/metadata/content-creators/{id}",
                                meta.creator.getId()))
                .andExpect(status().isNotFound());
    }
}
