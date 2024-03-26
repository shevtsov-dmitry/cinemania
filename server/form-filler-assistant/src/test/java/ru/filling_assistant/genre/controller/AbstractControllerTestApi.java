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
public abstract class AbstractControllerTestApi {

    @Autowired
    MockMvc mockMvc;
    final String ENDPOINT_URL = "/filling-assistants";
    String CONTROLLER_REQUEST_MAPPING;

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

    void getAllEntities() throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/get/all";

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(res -> {
                    String arrayOfEntities = gson.toJson(res.getResponse().getContentAsString());
                    System.out.println(arrayOfEntities);
                });
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
                    assertEquals(expectedSize, list.size());
                });

        url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/delete";
        mockMvc.perform(delete(url)
                        .contentType("application/json")
                        .content(namesToDeleteAfterSave))
                .andExpect(status().isOk())
                .andExpect(content().string("All requested entities have been deleted successfully."));
    }

    void checkSequenceRequest(String sequence, List<String> expected) throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/get/bySequence";

        mockMvc.perform(get(url)
                        .param("sequence", sequence))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(result ->
                        expected.stream().map(el -> "\"".concat(el).concat("\"")).toList().toString()
                                .equals(result.getResponse().getContentAsString()));
    }


}
