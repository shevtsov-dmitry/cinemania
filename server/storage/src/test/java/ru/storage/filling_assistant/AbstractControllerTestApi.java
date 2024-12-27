package ru.storage.filling_assistant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.storage.filling_assistant.genre.COMMON;

import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
public abstract class AbstractControllerTestApi {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    final String ENDPOINT_URL = "api/v0/filling-assistants";
    String CONTROLLER_REQUEST_MAPPING;

    static final String GENERATED_NAME;
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
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "all";

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(res -> objectMapper.readValue(res.getResponse().getContentAsString(), new TypeReference<List<String>>() {
                }));
    }

    void deleteOneEntity() throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/delete";
        List<String> singleton = Collections.singletonList(GENERATED_NAME);
        String json = objectMapper.writeValueAsString(singleton);

        mockMvc.perform(delete(url)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(not(blankString())));
    }

    void addMultipleEntities() throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/add/many";
        String json = objectMapper.writeValueAsString(FIVE_RANDOM_NAMES);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(res -> {
                    List<String> list = objectMapper.readValue(res.getResponse().getContentAsString(), new TypeReference<ArrayList<String>>() {
                    });
                    assertEquals(5, list.size());
                });
    }

    void deleteAddedEntities() throws Exception {
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/delete";
        String json = objectMapper.writeValueAsString(FIVE_RANDOM_NAMES);

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
                .andExpect(content().string(matchesPattern("\\d")));

        mockMvc.perform(post(url)
                        .param("name", GENERATED_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot save any, because all requested entities already exist in database."));

        url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/delete";
        List<String> singleton = List.of(GENERATED_NAME);
        String json = objectMapper.writeValueAsString(singleton);

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
        String namesToDeleteAfterSave = objectMapper.writeValueAsString(set);

        String json = objectMapper.writeValueAsString(genreNames);
        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/add/many";
        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andDo(res -> {
                    List<Long> list = objectMapper.readValue(res.getResponse().getContentAsString(), new TypeReference<ArrayList<Long>>() {
                    });
                    assertEquals(set.size(), list.size());
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
