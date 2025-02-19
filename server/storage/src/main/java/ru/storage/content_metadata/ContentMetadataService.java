package ru.storage.content_metadata;

import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.storage.content_metadata.poster.PosterService;
import ru.storage.content_metadata.video.VideoService;
import ru.storage.content_metadata.video.standalone.StandaloneVideoShowService;
import ru.storage.content_metadata.video.trailer.TrailerService;
import ru.storage.content_metadata.video.tv_series.TvSeriesService;
import ru.storage.exceptions.ParseIdException;
import ru.storage.person.filming_group.FilmingGroupService;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class ContentMetadataService {

  private static final Logger LOG = LoggerFactory.getLogger(ContentMetadataService.class);

  private final ContentMetadataRepo contentMetadataRepo;
  private final PosterService posterService;
  private final TrailerService trailerService;
  private final StandaloneVideoShowService standaloneVideoShowService;
  private final TvSeriesService tvSeriesService;
  private final VideoService videoService;
  private final FilmingGroupService filmingGroupService;

  public ContentMetadataService(
      VideoService videoService,
      ContentMetadataRepo contentMetadataRepo,
      PosterService posterService,
      TrailerService trailerService,
      StandaloneVideoShowService standaloneVideoShowService,
      TvSeriesService tvSeriesService,
      FilmingGroupService filmingGroupService) {
    this.videoService = videoService;
    this.contentMetadataRepo = contentMetadataRepo;
    this.posterService = posterService;
    this.trailerService = trailerService;
    this.standaloneVideoShowService = standaloneVideoShowService;
    this.tvSeriesService = tvSeriesService;
    this.filmingGroupService = filmingGroupService;
  }

  /**
   * Saves the bundle of metadata to the database.
   *
   * @param metadata the ContentMetadata object to be saved
   * @return the saved ContentMetadata object
   * @throws IllegalArgumentException if the metadata does not contain any video or episode
   */
  public ContentMetadata saveMetadata(ContentMetadata metadata) {
    if (metadata.getTrailer() != null) {
      metadata.setTrailer(trailerService.saveMetadata(metadata.getTrailer()));
    }

    if (metadata.getStandaloneVideoShow() != null) {
      metadata.setStandaloneVideoShow(
          standaloneVideoShowService.saveMetadata(metadata.getStandaloneVideoShow()));
    } else if (metadata.getTvSeries() != null) {
      metadata.setTvSeries(tvSeriesService.saveMetadata(metadata.getTvSeries()));
    } else {
      throw new IllegalArgumentException(
          "Метаданные должны содержать хотя бы одно видео или эпизод.");
    }

    var filmingGroupMetadata = metadata.getFilmingGroup();
    metadata.setFilmingGroup(filmingGroupService.saveMetadata(filmingGroupMetadata));

    return contentMetadataRepo.save(metadata);
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
  @Transactional
  public void removeContent(String contentId) {
    var metadata =
        contentMetadataRepo
            .findById(contentId)
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "Не удалось найти запрашиваемый материал по идентификатору."));
    posterService.deleteByIds(metadata.getPoster().getId());
      filmingGroupService.deleteById(metadata.getFilmingGroup().getId());
    if (metadata.getTrailer() != null) {
      String id = metadata.getTrailer().getId();
      trailerService.deleteMetadata(id);
      videoService.deleteTrailerByIds(id);
    }
    if (metadata.getStandaloneVideoShow() != null) {
      String id = metadata.getStandaloneVideoShow().getId();
      standaloneVideoShowService.deleteMetadata(id);
      videoService.deleteStandaloneVideoShowByIds(id);
    }
    // if (metadata.getTvSeries() != null) { }
    contentMetadataRepo.delete(metadata);
  }
}
