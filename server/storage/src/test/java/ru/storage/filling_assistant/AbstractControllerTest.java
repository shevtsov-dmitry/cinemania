package ru.storage.filling_assistant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.storage.filling_assistant.genre.CustomTestUtils;

import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${server.url}")
    protected String SERVER_URL;
    protected String ENDPOINT_URL;
    protected String CONTROLLER_REQUEST_MAPPING;

    protected static final String GENERATED_NAME;
    protected static final List<String> MULTIPLE_RANDOM_NAMES = new ArrayList<>(5);

    private record ExpectedJson(String name) {
    }

    @BeforeEach
    void setUp() {
        ENDPOINT_URL = SERVER_URL + "/api/v0/filling-assistants" + CONTROLLER_REQUEST_MAPPING;
    }

    static {
        try {
            GENERATED_NAME = CustomTestUtils.generateRandomHash().substring(0, 8);
            for (int i = 0; i < 5; i++) {
                MULTIPLE_RANDOM_NAMES.add(CustomTestUtils.generateRandomHash().substring(0, 10));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    void saveSingle() throws Exception {
        mockMvc.perform(post(ENDPOINT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExpectedJson(GENERATED_NAME))))
                .andExpect(status().isCreated());
    }


    void saveMultiple() throws Exception {
        List<ExpectedJson> multiple = MULTIPLE_RANDOM_NAMES.stream()
                .map(ExpectedJson::new)
                .toList();
        mockMvc.perform(post(ENDPOINT_URL + "/multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(multiple)))
                .andExpect(status().isCreated());
    }

    void getAll() throws Exception {
        mockMvc.perform(get(ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    List<String> list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertThat(list)
                            .contains(GENERATED_NAME)
                            .containsAll(MULTIPLE_RANDOM_NAMES);
                });
    }

    void deleteAllSaved() throws Exception {
        MULTIPLE_RANDOM_NAMES.add(GENERATED_NAME);
        for (var name : MULTIPLE_RANDOM_NAMES) {
            mockMvc.perform(delete(ENDPOINT_URL + "/" + name))
                    .andExpect(status().isNoContent());
        }
    }

}
