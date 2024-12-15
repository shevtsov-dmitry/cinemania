package ru.storage.objectstorage.poster;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.storage.content.objectstorage.poster.Poster;
import ru.storage.content.objectstorage.poster.PosterRepo;
import ru.storage.content.objectstorage.poster.PosterService;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class PosterServiceTest {

    @Autowired
    private PosterService posterService;
    @Autowired
    private PosterRepo posterRepo;

    private static final Poster testPosterMetadata = new Poster(null, "filename.jpg", "image/jpeg");

    @Test
    @Order(1)
    void saveMetadata() throws Exception {
        var savedPoster = posterService.saveMetadata(testPosterMetadata);
        assertNotNull(savedPoster);
        assertFalse(savedPoster.getId().isBlank());
        testPosterMetadata.setId(testPosterMetadata.getId());
    }

    @Test
    @Order(2)
    void getSavedMetadata() throws Exception {
        assertNotNull(testPosterMetadata.getId());
        Optional<Poster> optionalPoster = posterRepo.findById(testPosterMetadata.getId());
        assertNotNull(optionalPoster.orElse(null));
        assertEquals(testPosterMetadata, optionalPoster.get());
    }

    @Test
    @Order(3)
    void deleteMetadata() throws Exception {
        Method method = PosterService.class.getDeclaredMethod("deleteFromLocalDb", Set.class);
        method.setAccessible(true);
        method.invoke(posterService, Set.of(testPosterMetadata.getId()));
        method.setAccessible(false);
        Optional<Poster> optionalPoster = posterRepo.findById(testPosterMetadata.getId());
        assertNull(optionalPoster.orElse(null));
    }

}
