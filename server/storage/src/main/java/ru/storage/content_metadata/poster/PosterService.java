package ru.storage.content_metadata.poster;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
   * @param id saved metatada id from db
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
   * @apiNote This method supports both single and multiple content metadata IDs, separated by
   *     commas.
   * @param unparsedIds string of ids separated by comma
   * @return resouce containing single byte array with the delimiter in between the binary items.
   * @throws S3Exception when an error occurs during the retrieval process
   */
  public Resource getImagesMatchingMetadataIds(String unparsedIds) {
    return S3GeneralOperations.getItemsByIds(S3_FOLDER, unparsedIds);
  }

  /**
   * Get images from S3 cloud storage by IDs.
   *
   * @apiNote
   *     <p>This method supports both single and multiple content metadata IDs, separated by commas.
   * @param ids required ids
   */
  public Resource getImagesMatchingMetadataIds(Collection<String> ids) {
    return S3GeneralOperations.getItemsByIds(S3_FOLDER, ids);
  }

  /**
   * Delete related content instances from local metadata db and also from S3 storage.
   *
   * @param unparsedIds a comma-separated string of content metadata IDs
   * @throws ParseIdException when of invalid number format defined by api
   * @throws S3Exception when image wasn't deleted
   */
  public void deleteByIds(String unparsedIds) {
    List<String> ids = Arrays.asList(unparsedIds.split(",")).stream().map(String::trim).toList();
    if (ids.isEmpty()) {
      throw new ParseIdException();
    }
    ids.forEach(posterRepo::deleteById);
    S3GeneralOperations.deleteItems(S3_FOLDER, ids);
  }
}
