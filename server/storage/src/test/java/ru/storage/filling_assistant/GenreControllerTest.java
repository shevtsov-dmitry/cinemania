package ru.storage.filling_assistant;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenreControllerTest extends AbstractControllerTest {

    {
        super.CONTROLLER_REQUEST_MAPPING = "/genres";
    }

    @Test
    @Order(1)
    void saveSingle() throws Exception {
        super.saveSingle();
    }

    @Test
    @Order(2)
    void saveMultiple() throws Exception {
        super.saveMultiple();
    }

    @Test
    @Order(3)
    void getAll() throws Exception {
        super.getAll();
    }

    @Test
    @Order(4)
    void deleteAllSaved() throws Exception {
        super.deleteAllSaved();
    }

}
