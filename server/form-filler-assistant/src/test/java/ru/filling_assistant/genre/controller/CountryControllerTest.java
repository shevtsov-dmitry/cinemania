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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CountryControllerTest extends AbstractControllerTestApi {

    {
        super.CONTROLLER_REQUEST_MAPPING = "/countries";
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
        String[] countries = {"Кипр", "Киргизстан", "Китай", "Кувейт"};
        String json = gson.toJson(countries);

        String url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/add/many";

        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ArrayList<Double> savedCountriesIds = gson.fromJson(result.getResponse().getContentAsString(), ArrayList.class);
                    savedCountriesIds.stream().allMatch(el -> el instanceof Double);
                });

        checkSequenceRequest("К", List.of("Кипр", "Киргизстан", "Китай", "Кувейт"));
        checkSequenceRequest("Ки", List.of("Кипр", "Киргизстан", "Китай"));
        checkSequenceRequest("Кип", List.of("Кипр"));

        url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/get/bySequence";

        mockMvc.perform(get(url)
                        .param("sequence", "@#@@#JFWEO"))
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().equals("[]"));

        url = ENDPOINT_URL + CONTROLLER_REQUEST_MAPPING + "/delete";

        mockMvc.perform(delete(url)
                        .contentType("application/json")
                        .content(gson.toJson(countries)))
                .andExpect(status().isOk())
                .andExpect(content().string("All requested entities have been deleted successfully."));
    }
}
