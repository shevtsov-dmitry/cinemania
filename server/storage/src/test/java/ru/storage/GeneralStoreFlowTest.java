package ru.storage;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.storage.metadata.MetadataController;

import java.io.File;

/**
 * Tests behavior:
 * <ol>
 * <li>receive json with 3 objects</li>
 * <li>save their metadata into local db</li>
 * <li>save poster into S3</li>
 * <li>save video into S3 by chunks</li>
 * </ol>
 */
@WebMvcTest(controllers = {MetadataController.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class GeneralStoreFlowTest {

    @Autowired
    private MockMvc mockMvc;

    private static final File IMAGE_FILE = new File("src/test/java/ru/storage/assets/image.jpg");


    @Test
    @Order(1)
    void saveMetadata() throws Exception {
//        mockMvc.perform()
//        mockMvc.perform(post("/metadata"))
    }
}
