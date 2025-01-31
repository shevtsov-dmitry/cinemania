package ru.storage.content_metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ru.storage.content_metadata.poster.Poster;
import ru.storage.content_metadata.poster.PosterService;
import ru.storage.content_metadata.video.Video;
import ru.storage.content_metadata.video.VideoService;
import ru.storage.exceptions.ParseIdException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ContentMetadataService {

  private static final Logger log = LoggerFactory.getLogger(ContentMetadataService.class);
  private final ContentMetadataRepo contentMetadataRepo;
  private final VideoService videoService;
  private final PosterService posterService;

  public ContentMetadataService(
      ContentMetadataRepo contentMetadataRepo,
      VideoService videoService,
      PosterService posterService) {
    this.contentMetadataRepo = contentMetadataRepo;
    this.videoService = videoService;
    this.posterService = posterService;
  }

  /**
   * @param metadataObjects {@link VideoInfoParts} metadata record of {@link Video}, {@link Poster}
   *     and {@link ContentMetadata}
   * @return {@link VideoInfoParts} object
   * @throws IllegalArgumentException when content type is wrong
   */
  public ContentMetadata saveMetadata(VideoInfoParts metadataObjects) {
    Video savedVideoMetadata = videoService.saveMetadata(metadataObjects.video());
    Poster savedPosterMetadata = posterService.saveMetadata(metadataObjects.poster());
    ContentMetadata contentMetadata = metadataObjects.contentMetadata();
    if (contentMetadata == null) {
      log.warn("Error saving contentDetails object from request, because it is null.");
      throw new IllegalArgumentException(
          "Необходимые сведения о загружаемом видео-проекте отсутствуют");
    }

    contentMetadata.setSingleShowVideo(savedVideoMetadata);
    contentMetadata.setPoster(savedPosterMetadata);
    return contentMetadataRepo.save(contentMetadata);
  }

  // TODO Probably need to add exception handling if requested more than existed.

  /**
   * Get recently added list of metadata {@link ContentMetadata}.
   *
   * @param amount requested amount
   * @return list of metadata objects
   */
  public List<ContentMetadata> getRecentlyAdded(int amount) {
    return contentMetadataRepo.findByOrderByCreatedAtDesc(Pageable.ofSize(amount));
  }

  /**
   * Delete all content related instances from local metadata db and also from S3 storage.
   *
   * @param contentId contentId from local db
   * @throws NoSuchElementException when element not found
   * @throws ParseIdException when of invalid number format defined by api
   * @throws S3Exception when image wasn't deleted
   */
  public void removeContent(String contentId) {
    var contentDetails =
        contentMetadataRepo
            .findById(contentId)
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "Не удалось найти запрашиваемый материал по идентификатору."));
    posterService.deleteByIds(contentDetails.getPoster().getId());
    videoService.deleteByIds(contentDetails.getSingleShowVideo().getId());
    contentMetadataRepo.delete(contentDetails);
  }

  //    /**
  //     * Retrieve recently added video info parts bundle
  //     *
  //     * @param amount how many instances need to be searched
  //     * @return recently added {@code VideoInfoParts} bundle
  //     */
  //    public List<VideoInfoParts> getRecentlyAdded(int amount) {
  //        List<ContentMetadata> recentlyAdded =
  // contentMetadataRepo.findRecentlyAdded(Pageable.ofSize(amount));
  //        return recentlyAdded.stream()
  //                .map(contentMetadata -> {
  //                    long id = contentMetadata.getId();
  //                    return new VideoInfoParts(
  //                            contentMetadata,
  //                            videoService.findByContentMetadataId(id).orElse(new Video()),
  //                            posterService.findByContentMetadataId(id).orElse(new Poster())
  //                    );
  //                }).toList();
  //    }

}
