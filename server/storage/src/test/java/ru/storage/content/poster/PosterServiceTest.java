package ru.storage.content.poster;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
class PosterServiceTest {

  @Autowired private PosterService posterService;
  @MockBean private PosterRepo posterRepo;

  private static final File IMAGE_FILE = new File("src/test/java/ru/storage/assets/image.jpg");

  // === POSSITIVE TESTS ===

  @Test
  void saveMetadata() throws Exception {
    Poster input = new Poster(null, "image.jpg", "image/jpeg");
    Poster saved = new Poster("abc123", "image.jpg", "image/jpeg");
    when(posterRepo.save(any(Poster.class))).thenReturn(saved);
    var result = posterService.saveMetadata(input);
    assertThat(result).isNotNull().isEqualTo(saved);
  }

  @Test
  void uploadPoster_ReturnsCompressed() throws Exception {
    Poster metadata = new Poster("abc123", "image.jpg", "image/jpeg");
    byte[] testImageFile = Files.readAllBytes(IMAGE_FILE.toPath());
    var image = new MockMultipartFile("image", "image.jpg", "image/jpeg", testImageFile);
    posterService.uploadImage(metadata.getId(), image);
    var resourse = posterService.getImagesMatchingMetadataIds(metadata.getId());
    assertThat(resourse.getContentAsByteArray())
        .isNotNull()
        .isNotEmpty()
        .hasSizeLessThan(testImageFile.length); // Due to image compression
  }

  // === NEGATIVE TESTS ===

}
