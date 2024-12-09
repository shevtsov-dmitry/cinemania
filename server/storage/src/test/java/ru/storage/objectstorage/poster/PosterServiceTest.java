package ru.storage.objectstorage.poster;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.storage.metadata.ContentMetadataRepo;
import ru.storage.metadata.objectstorage.poster.PosterController;
import ru.storage.metadata.objectstorage.poster.PosterRepo;
import ru.storage.metadata.objectstorage.poster.PosterService;

@ExtendWith(MockitoExtension.class)
class PosterServiceTest {

    @Mock
    private ContentMetadataRepo contentMetadataRepo;
    @Mock
    private PosterRepo posterRepo;
    @Mock
    private PosterController posterController;
    @InjectMocks
    private PosterService posterService;

    private static Long metadataId;

    @BeforeAll
    static void setUp() {

    }

    @AfterAll
    static void tearDown() {
    }

    @Test
    void savePoster_Image_success() {
    }

    @Test
    void getImagesMatchingMetadataIds_success() {
    }

    @Test
    void updateExistingImage_success() {
    }

    @Test
    void deleteByIds_success() {
    }
}
