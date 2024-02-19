package ru.filling_assistant.genre.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenreControllerTest extends AbstractControllerTestApi {

    {
        super.CONTROLLER_REQUEST_MAPPING = "/genres";
    }

    @Test
    @Order(1)
    void addOneEntity() throws Exception {
        super.addOneEntity();
    }

    @Test
    @Order(2)
    void getOneEntity() throws Exception {
        super.getOneEntity();
    }

    @Test
    @Order(3)
    void deleteOneEntity() throws Exception {
        super.deleteOneEntity();
    }

    @Test
    @Order(4)
    void addMultipleEntities() throws Exception {
        super.addMultipleEntities();
    }

    @Test
    @Order(5)
    void deleteAddedEntities() throws Exception {
        super.deleteAddedEntities();
    }

    @Test
    void addOneEntityMoreThanOnce_thenDelete() throws Exception {
        super.addOneEntityMoreThanOnce_thenDelete();
    }

    @Test
    void addEntitiesWithDups_thenCleanup() throws Exception {
        super.addEntitiesWithDups_thenCleanup();
    }

    @Test
    @Override
    public void findEntityNamesBySequences() throws Exception {
        String[] genres = {"Драма", "Драматургия", "Другое", "Дружба"};
        String json = gson.toJson(genres);

        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/add/many";

        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ArrayList<Double> savedGenreIds = gson.fromJson(result.getResponse().getContentAsString(), ArrayList.class);
                    savedGenreIds.stream().allMatch(el -> el instanceof Double);
                });

        checkSequenceRequest("Д", List.of("Драма", "Драматургия", "Другое", "Дружба"));
        checkSequenceRequest("Др", List.of("Драма", "Драматургия", "Другое", "Дружба"));
        checkSequenceRequest("Дру", List.of("Другое", "Дружба"));
        checkSequenceRequest("Друж", List.of("Дружба"));
        checkSequenceRequest("Дра", List.of("Драма", "Драматургия"));

        url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/get/bySequence";

        mockMvc.perform(get(url)
                .param("sequence", "@#@@#JFWEO"))
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().equals("[]"));

        url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/delete";

        mockMvc.perform(delete(url)
                        .contentType("application/json")
                        .content(gson.toJson(genres)))
                .andExpect(status().isOk())
                .andExpect(content().string("All requested entities have been deleted successfully."));
    }

}
