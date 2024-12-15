package ru.storage.content;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.storage.content.poster.PosterMetadata;
import ru.storage.content.poster.PosterRepo;
import ru.storage.content.video.VideoMetadata;
import ru.storage.content.video.VideoRepo;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MetadataServiceTest {

    private static VideoInfoParts testMetadata;

    @Autowired
    private VideoRepo videoRepo;
    @Autowired
    private PosterRepo posterRepo;
    @Autowired
    private ContentService contentService;
    @Autowired
    private ContentDetailsRepo contentDetailsRepo;

    @BeforeAll
    static void setUp() {
        EnhancedRandom randomData = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .excludeField(field -> field.getName().equals("id"))
                .collectionSizeRange(1, 5)
                .build();

        testMetadata = new VideoInfoParts(
                randomData.nextObject(ContentDetails.class, "video", "poster", "createdAt"),
                randomData.nextObject(VideoMetadata.class, "contentMetadata"),
                randomData.nextObject(PosterMetadata.class, "contentMetadata")
        );
        testMetadata.videoMetadata().setContentType("video/mp4");
        testMetadata.posterMetadata().setContentType("image/jpeg");

    }

}