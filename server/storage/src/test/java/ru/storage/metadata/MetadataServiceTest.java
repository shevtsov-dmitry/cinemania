package ru.storage.metadata;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MetadataServiceTest {
    @Autowired
    private MetadataService metadataService;
    @Autowired
    private MetadataRepo metadataRepo;

    private static Content testMetadata;

    @BeforeAll
    static void setUp() {
        EnhancedRandom randomData = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .excludeField(field -> field.getName().equals("id"))
                .collectionSizeRange(1, 5)
                .build();
        testMetadata = randomData.nextObject(Content.class);
    }

    @Test
    void saveMetadata() {

    }

    @Test
    void getMetadataById() {
    }

    @Test
    void deleteMetadataById() {
    }
}