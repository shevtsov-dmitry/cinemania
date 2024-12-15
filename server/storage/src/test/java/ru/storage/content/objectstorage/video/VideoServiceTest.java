package ru.storage.content.objectstorage.video;

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

    private static final Video testVideoMetadata = new Video(null, "video.mp4", "video/mp4");

    @Test
    @Order(1)
    void saveMetadata() throws Exception {
        var savedPoster = videoService.saveMetadata(testVideoMetadata);
        assertNotNull(savedPoster);
        assertFalse(savedPoster.getId().isBlank());
        testVideoMetadata.setId(testVideoMetadata.getId());
    }

    @Test
    @Order(2)
    void getSavedMetadata() throws Exception {
        assertNotNull(testVideoMetadata.getId());
        var optionalVideo = videoRepo.findById(testVideoMetadata.getId());
        assertNotNull(optionalVideo.orElse(null));
        assertEquals(testVideoMetadata, optionalVideo.get());
    }

    @Test
    @Order(3)
    void deleteMetadata() throws Exception {
        Method method = VideoService.class.getDeclaredMethod("deleteFromLocalDb", Set.class);
        method.setAccessible(true);
        method.invoke(videoService, Set.of(testVideoMetadata.getId()));
        method.setAccessible(false);
        var optionalVideo = videoRepo.findById(testVideoMetadata.getId());
        assertNull(optionalVideo.orElse(null));
    }

}
