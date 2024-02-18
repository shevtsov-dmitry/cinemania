package ru.filling_assistant.genre.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.filling_assistant.genre.COMMON;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
public class AbstractControllerTestApi {

    @Autowired
    private MockMvc mockMvc;
    private final String ENDPOINT_URL = "/fillingAssistants";
    protected String CONTROLLER_REQUEST_MAPPING;

    static final String GENERATED_NAME;
    static final Gson gson = new Gson();
    static final List<String> FIVE_RANDOM_NAMES = new ArrayList<>(5);

    static {
        try {
            GENERATED_NAME = COMMON.generateRandomHash().substring(0, 8);
            for (int i = 0; i < 5; i++) {
                FIVE_RANDOM_NAMES.add(COMMON.generateRandomHash().substring(0, 10));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    void addOneEntity() throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/add/one";

        mockMvc.perform(post(url)
                        .param("name", GENERATED_NAME))
                .andExpect(status().isOk())
                .andExpect(content().string(not(blankString())))
                .andDo(res -> Long.parseLong(res.getResponse().getContentAsString()));
    }

    void getOneEntity() throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/get/bySequence";

        mockMvc.perform(get(url)
                        .param("sequence", GENERATED_NAME))
                .andExpect(status().isOk())
                .andExpect(res -> content().string(gson.toJson(GENERATED_NAME)));
    }

    void deleteOneEntity() throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/delete";
        List<String> singleton = List.of(GENERATED_NAME);
        String json = gson.toJson(singleton);

        mockMvc.perform(delete(url)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("All requested entities have been deleted successfully."));
    }

    void addMultipleEntities() throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/add/many";
        String json = gson.toJson(FIVE_RANDOM_NAMES);

        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(res -> {
                    String contentAsString = res.getResponse().getContentAsString();
                    ArrayList<String> arrayList = gson.fromJson(contentAsString, ArrayList.class);
                    long count = arrayList.stream().mapToLong(Long::parseLong).count();
                    assertEquals(5, count);
                });
    }

    void deleteAddedEntities() throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/delete";
        String json = gson.toJson(FIVE_RANDOM_NAMES);

        mockMvc.perform(delete(url)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("All requested entities have been deleted successfully."));
    }


    void addOneEntityMoreThanOnce_thenDelete() throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/add/one";

        mockMvc.perform(post(url)
                        .param("name", GENERATED_NAME))
                .andExpect(status().isOk())
                .andExpect(res -> Long.parseLong(res.getResponse().getContentAsString()));

        mockMvc.perform(post(url)
                        .param("name", GENERATED_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot save any, because all requested entities already exist in database."));

        url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/delete";
        List<String> singleton = List.of(GENERATED_NAME);
        String json = gson.toJson(singleton);

        mockMvc.perform(delete(url)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("All requested entities have been deleted successfully."));
    }


    void addEntitiesWithDups_thenCleanup() throws Exception {
        List<String> genreNames = new ArrayList<>(FIVE_RANDOM_NAMES);
        for (int i = 0; i < 3; i++) {
            genreNames.add(genreNames.getFirst());
        }
        Set<String> set = new HashSet<>(List.copyOf(FIVE_RANDOM_NAMES));
        String namesToDeleteAfterSave = gson.toJson(set);

        String json = gson.toJson(genreNames);
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/add/many";
        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andDo(res -> {
                    int expectedSize = set.size();
                    List<Long> list = gson.fromJson(res.getResponse().getContentAsString(), List.class);
                    assertEquals(expectedSize,list.size());
                });

        url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/delete";
        mockMvc.perform(delete(url)
                        .contentType("application/json")
                        .content(namesToDeleteAfterSave))
                .andExpect(status().isOk())
                .andExpect(content().string("All requested entities have been deleted successfully."));
    }


//    @Test
//    void addGenresWithDupsThenAgainButWithNewElements() {
//        List<String> genreNames = List.of("Сказки", "Короткометражные", "Образовательные", "Сказки", "Сказки", "Образовательные");
//        List<Genre> newGenres = List.of(new Genre("NEW"), new Genre("ELEMENTS"), new Genre("TESTING"));
//        List<Genre> genres = genreNames.stream().map(Genre::new).toList();
//        service.saveWithoutDuplicates(genres);
//        List<String> genreNamesWithNewEls = new ArrayList<>();
//        genreNamesWithNewEls.addAll(genreNames);
//        genreNamesWithNewEls.addAll(newGenres.stream().map(Genre::getName).toList());
//        List<Genre> genresWithNewEls = new ArrayList<>(genreNamesWithNewEls.stream().map(Genre::new).toList());
//        service.saveWithoutDuplicates(genresWithNewEls);
//        for (Genre genre : newGenres) {
//            assertNotNull(repo.findByName(genre.getName()));
//        }
//    }
//
//    @RepeatedTest(2)
//    void insertOneGenreMoreThanOnce() {
//        Genre genre = new Genre("комедия");
//        service.saveWithoutDuplicates(new ArrayList<>(List.of(genre)));
//        String answer = service.saveWithoutDuplicates(new ArrayList<>(List.of(genre)));
//        assertEquals(answer, "Cannot save because already exist in database.");
//        assertNotNull(repo.findByName(genre.getName()));
//    }
//
//    @Test
//    void FindGenreNamesByPreparedSequences() {
//        List<Genre> genres = List.of(new Genre("Драма"), new Genre("Драматургия"), new Genre("Другое"), new Genre("Дружба"));
//        repo.saveAll(genres);
//
//        List<String> case1Actual = service.findMatchedGenres("Д");
//        List<String> case1Expected = List.of("Драма", "Драматургия", "Другое", "Дружба");
//
//        List<String> case2Actual = service.findMatchedGenres("Др");
//        List<String> case2Expected = List.of("Драма", "Драматургия", "Другое", "Дружба");
//
//        List<String> case3Actual = service.findMatchedGenres("Дру");
//        List<String> case3Expected = List.of("Другое", "Дружба");
//
//        List<String> case4Actual = service.findMatchedGenres("Друж");
//        List<String> case4Expected = List.of("Дружба");
//
//        List<String> case5Actual = service.findMatchedGenres("Дра");
//        List<String> case5Expected = List.of("Драма", "Драматургия");
//
//        assertLinesMatch(case1Expected, case1Actual);
//        assertLinesMatch(case2Expected, case2Actual);
//        assertLinesMatch(case3Expected, case3Actual);
//        assertLinesMatch(case4Expected, case4Actual);
//        assertLinesMatch(case5Expected, case5Actual);
//    }
//
//    @Test
//    void FindNotMoreThanFiveGenreNamesBySequence() {
//        List<Genre> genres = List.of(
//                new Genre("Драма"), new Genre("Драматургия"),
//                new Genre("Другое"), new Genre("Дружба"),
//                new Genre("Дорама"), new Genre("Диджитал"),
//                new Genre("Догма"), new Genre("Дэнс")
//        );
//        repo.saveAll(genres);
//        List<String> genresExpected = List.of("Драма", "Драматургия", "Другое", "Дружба", "Дорама");
//        List<String> genresActual = service.findMatchedGenres("Д");
//        assertLinesMatch(genresExpected, genresActual);
//    }


}
