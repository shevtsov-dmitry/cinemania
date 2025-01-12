package ru.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.condition.MediaTypeExpression;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.storage.content.content_creators.Actor;
import ru.storage.content.content_creators.ContentCreator;
import ru.storage.content.content_creators.ContentCreatorKind;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.File;

import javax.swing.text.AbstractDocument.Content;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContentCreatorFlowTest {
    @Value("${server.url}")
    private String serverUrl;
    private String endpointUrl;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static final File IMAGE_FILE = new File("src/test/java/ru/storage/assets/image.jpg");

    @BeforeEach
    void setUp() {
        endpointUrl = serverUrl + "/content-creators";
    }

    @Test
    @Order(1)
    void saveContentCreatorInfo() throws Exception {
        ContentCreator creator = new Actor();
        creator.setFullname("Тестовое Имя");
        creator.setFullnameEng("Test Fullname");
        creator.setBornPlace("Hartford, Connecticut, USA");
        creator.setHeightMeters(1.7D);
        creator.setContentCreatorKind(ContentCreatorKind.ACTOR);

        mockMvc.perform(post(endpointUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(creator)))
        .andExpect(content().string(is(not(emptyString()))));
    }

    @Test
    @Order(2)
    void uploadUserPic() throws Exception {

        // mockMvc.perform(post(endpointUrl + "/user-pic/upload"))
        // .andExpect();
    }

    // @Test
    // @Order(1)
    // void name() throws Exception {}
    // @Test
    // @Order(1)
    // void name() throws Exception {}
    // @Test
    // @Order(1)
    // void name() throws Exception {}
    // @Test
    // @Order(1)
    // void name() throws Exception {}
    // @Test
    // @Order(1)
    // void name() throws Exception {}
    // @Test
    // @Order(1)
    // void name() throws Exception {}
    // @Test
    // @Order(1)
    // void name() throws Exception {}
}
