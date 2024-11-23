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

    private static VideoInfoParts testMetadata;

    @Autowired
    private MetadataService metadataService;
    @Autowired
    private VideoRepo videoRepo;
    @Autowired
    private PosterRepo posterRepo;
    @Autowired
    private ContentMetadataRepo contentMetadataRepo;

    @BeforeAll
    static void setUp() {
        EnhancedRandom randomData = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .excludeField(field -> field.getName().equals("id"))
                .collectionSizeRange(1, 5)
                .build();

        testMetadata = new VideoInfoParts(
                randomData.nextObject(ContentMetadata.class, "video", "poster", "createdAt"),
                randomData.nextObject(Video.class, "content"),
                randomData.nextObject(Poster.class, "content")
        );
        testMetadata.video().setContentType("video/mp4");
        testMetadata.poster().setContentType("image/jpeg");
    }

    @Test
    void save_ok() {

        long metadataId = metadataService.saveMetadata(testMetadata);

        Optional<Video> savedVideo = videoRepo.findByContentMetadataId(metadataId);
        Optional<Poster> savedPoster = posterRepo.findByContentMetadataId(metadataId);
        Optional<ContentMetadata> savedContentMetadata = contentMetadataRepo.findContentMetadataById(metadataId);

        assertTrue(savedVideo.isPresent());
        assertTrue(savedPoster.isPresent());
        assertTrue(savedContentMetadata.isPresent());

        assertNotNull(savedVideo.get().getContentMetadata());
        assertNotNull(savedPoster.get().getContentMetadata());
        assertNotNull(savedContentMetadata.get().getPoster());
        assertNotNull(savedContentMetadata.get().getVideo());
    }
}