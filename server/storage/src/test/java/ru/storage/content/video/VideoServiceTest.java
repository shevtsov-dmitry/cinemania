package ru.storage.content.video;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class VideoServiceTest {

    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoRepo videoRepo;

    private static final Video TEST_VIDEO_METADATA_METADATA = new Video(null, "video.mp4", "video/mp4");

    @Test
    @Order(1)
    void saveMetadata() throws Exception {
        var savedPoster = videoService.saveMetadata(TEST_VIDEO_METADATA_METADATA);
        assertNotNull(savedPoster);
        assertFalse(savedPoster.getId().isBlank());
        TEST_VIDEO_METADATA_METADATA.setId(TEST_VIDEO_METADATA_METADATA.getId());
    }

    @Test
    @Order(2)
    void getSavedMetadata() throws Exception {
        assertNotNull(TEST_VIDEO_METADATA_METADATA.getId());
        var optionalVideo = videoRepo.findById(TEST_VIDEO_METADATA_METADATA.getId());
        assertNotNull(optionalVideo.orElse(null));
        assertEquals(TEST_VIDEO_METADATA_METADATA, optionalVideo.get());
    }

    @Test
    @Order(3)
    void deleteMetadata() throws Exception {
        Method method = VideoService.class.getDeclaredMethod("deleteFromLocalDb", Set.class);
        method.setAccessible(true);
        method.invoke(videoService, Set.of(TEST_VIDEO_METADATA_METADATA.getId()));
        method.setAccessible(false);
        var optionalVideo = videoRepo.findById(TEST_VIDEO_METADATA_METADATA.getId());
        assertNull(optionalVideo.orElse(null));
    }

}
