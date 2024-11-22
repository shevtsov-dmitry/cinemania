package ru.storage.metadata;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import jakarta.persistence.SqlResultSetMapping;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;
import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.poster.PosterRepo;
import ru.storage.metadata.objectstorage.video.Video;
import ru.storage.metadata.objectstorage.video.VideoRepo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MetadataServiceTest {
    @Autowired
    private MetadataService metadataService;

    private static VideoInfoPartsTuple testMetadata;
    @Autowired
    private VideoRepo videoRepo;
    @Autowired
    private PosterRepo posterRepo;
    @Autowired
    private MetadataRepo metadataRepo;

    @BeforeAll
    static void setUp() {
        EnhancedRandom randomData = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .excludeField(field -> field.getName().equals("id"))
                .collectionSizeRange(1, 5)
                .build();
        testMetadata = randomData.nextObject(VideoInfoPartsTuple.class);
    }

    @Test
    void save_ok() {
        metadataService.saveMetadata(testMetadata);
        Optional<Video> savedVideo = videoRepo.findOne(Example.of(testMetadata.video()));
        Optional<Poster> savedPoster = posterRepo.findOne(Example.of(testMetadata.poster()));
        Optional<Content> savedContentMetadata = metadataRepo.findOne(Example.of(testMetadata.content()));

        assertTrue(savedVideo.isPresent());
        assertTrue(savedPoster.isPresent());
        assertTrue(savedContentMetadata.isPresent());

        assertNotNull(savedVideo.get().getContent());
        assertNotNull(savedPoster.get().getContent());
        assertNotNull(savedContentMetadata.get().getPoster());
        assertNotNull(savedContentMetadata.get().getVideo());

    }
}