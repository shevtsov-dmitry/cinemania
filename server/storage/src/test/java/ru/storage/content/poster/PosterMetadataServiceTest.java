package ru.storage.content.poster;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.storage.content.poster.PosterMetadata;
import ru.storage.content.poster.PosterRepo;
import ru.storage.content.poster.PosterService;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class PosterMetadataServiceTest {

    @Autowired
    private PosterService posterService;
    @Autowired
    private PosterRepo posterRepo;

    private static final PosterMetadata TEST_POSTER_METADATA_METADATA = new PosterMetadata(null, "filename.jpg", "image/jpeg");

    @Test
    @Order(1)
    void saveMetadata() throws Exception {
        var savedPoster = posterService.saveMetadata(TEST_POSTER_METADATA_METADATA);
        assertNotNull(savedPoster);
        assertFalse(savedPoster.getId().isBlank());
        TEST_POSTER_METADATA_METADATA.setId(TEST_POSTER_METADATA_METADATA.getId());
    }

    @Test
    @Order(2)
    void getSavedMetadata() throws Exception {
        assertNotNull(TEST_POSTER_METADATA_METADATA.getId());
        Optional<PosterMetadata> optionalPoster = posterRepo.findById(TEST_POSTER_METADATA_METADATA.getId());
        assertNotNull(optionalPoster.orElse(null));
        assertEquals(TEST_POSTER_METADATA_METADATA, optionalPoster.get());
    }

    @Test
    @Order(3)
    void deleteMetadata() throws Exception {
        Method method = PosterService.class.getDeclaredMethod("deleteFromLocalDb", Set.class);
        method.setAccessible(true);
        method.invoke(posterService, Set.of(TEST_POSTER_METADATA_METADATA.getId()));
        method.setAccessible(false);
        Optional<PosterMetadata> optionalPoster = posterRepo.findById(TEST_POSTER_METADATA_METADATA.getId());
        assertNull(optionalPoster.orElse(null));
    }

}
