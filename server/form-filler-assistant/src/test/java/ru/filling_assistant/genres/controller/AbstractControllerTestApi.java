package ru.filling_assistant.genres.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import ru.filling_assistant.genres.COMMON;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class AbstractControllerTestApi {
    @Autowired
    private MockMvc mockMvc;
    private final String ENDPOINT_URL;
    private final String CONTROLLER_REQUEST_MAPPING;
    protected AbstractControllerTestApi(String endpointUrl, String controllerRequestMapping) {
        ENDPOINT_URL = endpointUrl;
        CONTROLLER_REQUEST_MAPPING = controllerRequestMapping;
    }

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

}
