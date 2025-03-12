package ru.storage.content_metadata.poster;

import jakarta.annotation.PostConstruct;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.exceptions.ParseIdException;
import ru.storage.utils.S3GeneralOperations;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class PosterService {

  @Value("${custom.s3.BUCKET_NAME}")
  private String bucketName;

  private static final String S3_FOLDER = "posters";
  private final PosterRepo posterRepo;

  public PosterService(PosterRepo posterRepo) {
    this.posterRepo = posterRepo;
  }

  @PostConstruct
  public void init() {
    Assert.notNull(
        bucketName,
        "переменная BUCKET_NAME должна быть указана в конфигурации application.properties.");
  }

  public Poster saveMetadata(Poster poster) {
    return posterRepo.save(poster);
  }

  /**
   * Upload image to S3 cloud storage.
   *
   * @param id saved metadata id from db
   * @param image multipart file
   * @throws IllegalArgumentException when multipart file is not an image
   * @throws S3Exception when image wasn't saved to S3 cloud storage
   */
  public void uploadImage(String id, MultipartFile image) {
    S3GeneralOperations.uploadImage(S3_FOLDER, id, image);
  }

  /**
   * Get images from S3 cloud storage by IDs.
   *
   * @param ids required ids
   * @return maps list with id and corresponding base 64 image
   * @throws S3Exception when an error occurs during the retrieval process
   * @apiNote This method supports both single and multiple content metadata IDs, separated by
   *     commas.
   */
  public List<Map<String, Serializable>> getImagesMatchingMetadataIds(List<String> ids) {
    return S3GeneralOperations.getMapsByIds(S3_FOLDER, ids);
  }

  /**
   * Get images from S3 cloud storage by IDs.
   *
   * @param ids required ids
   * @apiNote
   *     <p>This method supports both single and multiple content metadata IDs, separated by commas.
   */
  public Resource getImagesMatchingMetadataIds(Collection<String> ids) {
    return S3GeneralOperations.getBinariesByIds(S3_FOLDER, ids);
  }

  /**
   * Delete related content instances from local metadata db and also from S3 storage.
   *
   * @param ids required ids
   * @throws ParseIdException when of invalid number format defined by api
   * @throws S3Exception when image wasn't deleted
   */
  public void deleteByIds(List<String> ids) {
    if (ids.isEmpty()) {
      throw new ParseIdException();
    }
    ids.forEach(posterRepo::deleteById);
    S3GeneralOperations.deleteItems(S3_FOLDER, ids);
  }
}
