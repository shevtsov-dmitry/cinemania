package ru.filling_assistant.genre.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.is;

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
    void getAllEntities() throws Exception {
        super.getAllEntities();
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


}
