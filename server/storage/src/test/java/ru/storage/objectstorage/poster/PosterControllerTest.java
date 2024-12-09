package ru.storage.objectstorage.poster;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.storage.metadata.objectstorage.poster.PosterService;
import ru.storage.metadata.objectstorage.poster.PosterController;

import java.io.File;
import java.nio.file.Files;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PosterController.class)
@ExtendWith(MockitoExtension.class)
class PosterControllerTest {

    private static final String MESSAGE_HEADER = "Message";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PosterService posterService;
    @InjectMocks
    private PosterController posterController;

    private static final File IMAGE_FILE = new File("src/test/java/ru/storage/assets/image.jpg");
    private static final String ENDPOINT_URL = "/api/v0/posters";
    private static final Random RANDOM = new Random();


}