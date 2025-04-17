package ru.storage.content_metadata;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.storage.content_metadata.common.MediaFileInfo;
import ru.storage.content_metadata.country.Country;
import ru.storage.content_metadata.country.CountryRepo;
import ru.storage.content_metadata.genre.Genre;
import ru.storage.content_metadata.genre.GenreRepo;
import ru.storage.content_metadata.poster.Poster;
import ru.storage.content_metadata.poster.PosterService;
import ru.storage.content_metadata.video.VideoUploaderService;
import ru.storage.content_metadata.video.standalone.StandaloneVideoShow;
import ru.storage.content_metadata.video.standalone.StandaloneVideoShowService;
import ru.storage.content_metadata.video.trailer.Trailer;
import ru.storage.content_metadata.video.trailer.TrailerService;
import ru.storage.content_metadata.video.tv_series.TvSeriesService;
import ru.storage.exceptions.ParseIdException;
import ru.storage.person.filming_group.FilmingGroup;
import ru.storage.person.filming_group.FilmingGroupService;
import ru.storage.utils.ProjectStandardUtils;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class ContentMetadataService {

    private final ContentMetadataRepo contentMetadataRepo;
    private final PosterService posterService;
    private final TrailerService trailerService;
    private final StandaloneVideoShowService standaloneVideoShowService;
    private final TvSeriesService tvSeriesService;
    private final VideoUploaderService videoUploaderService;
    private final FilmingGroupService filmingGroupService;
    private final GenreRepo genreRepo;
    private final CountryRepo countryRepo;

    public ContentMetadataService(
            ContentMetadataRepo contentMetadataRepo,
            PosterService posterService,
            TrailerService trailerService,
            StandaloneVideoShowService standaloneVideoShowService,
            TvSeriesService tvSeriesService,
            VideoUploaderService videoUploaderService,
            FilmingGroupService filmingGroupService,
            GenreRepo genreRepo,
            CountryRepo countryRepo) {
        this.contentMetadataRepo = contentMetadataRepo;
        this.posterService = posterService;
        this.trailerService = trailerService;
        this.standaloneVideoShowService = standaloneVideoShowService;
        this.tvSeriesService = tvSeriesService;
        this.videoUploaderService = videoUploaderService;
        this.filmingGroupService = filmingGroupService;
        this.genreRepo = genreRepo;
        this.countryRepo = countryRepo;
    }

    /**
     * Saves the bundle of metadata to the database.
     *
     * @param metadata the ContentMetadata object to be saved
     * @return the saved ContentMetadata object
     * @throws IllegalArgumentException if the metadata does not contain any video
     *                                  or episode
     */
    public ContentMetadata saveMetadata(ContentMetadata metadata) {
        metadata.setFilmingGroup(filmingGroupService.saveMetadata(metadata.getFilmingGroup()));

        if (metadata.getTrailer() != null) {
            metadata.setTrailer(trailerService.saveMetadata(metadata.getTrailer()));
        }
        if (metadata.getPoster() != null) {
            metadata.setPoster(posterService.saveMetadata(metadata.getPoster()));
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

        return contentMetadataRepo.save(metadata);
    }

    public ContentMetadata saveMetadataFromDTO(ContentMetadataDTO dto) {

        Country country = countryRepo
                .findByName(dto.getCountryName())
                .orElseGet(() -> countryRepo.save(new Country(dto.getCountryName())));

        Genre mainGenre = genreRepo
                .findByName(dto.getMainGenreName())
                .orElseGet(() -> genreRepo.save(new Genre(dto.getMainGenreName())));

        List<Genre> subGenres = dto.getSubGenresNames().stream()
                .map(
                        name -> genreRepo.findByName(name).orElseGet(() -> genreRepo.save(new Genre(name))))
                .toList();

        FilmingGroup filmingGroup = filmingGroupService.saveMetadataFromDTO(dto.getFilmingGroupDTO());

        MediaFileInfo posterFile = dto.getPoster();
        var poster = new Poster(posterFile.filename(), posterFile.contentType(), posterFile.size());
        poster = posterService.saveMetadata(poster);

        MediaFileInfo filmFile = dto.getStandaloneVideoShow();
        var standaloneVideoShow = new StandaloneVideoShow(filmFile.filename(), filmFile.contentType(), filmFile.size());
        standaloneVideoShow = standaloneVideoShowService.saveMetadata(standaloneVideoShow);

        MediaFileInfo trailerFile = dto.getTrailer();
        var trailer = new Trailer(trailerFile.filename(), trailerFile.contentType(), trailerFile.size());
        trailer = trailerService.saveMetadata(trailer);

        var contentMetadata = ContentMetadata.builder()
                .title(dto.getTitle())
                .releaseDate(
                        LocalDate.parse(
                                dto.getReleaseDate(),
                                DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.of("ru-RU"))))
                .country(country)
                .mainGenre(mainGenre)
                .subGenres(subGenres)
                .description(dto.getDescription())
                .age(dto.getAge())
                .rating(dto.getRating())
                .filmingGroup(filmingGroup)
                .poster(poster)
                .trailer(trailer)
                .standaloneVideoShow(standaloneVideoShow)
                // TODO extremely important to add episodes parser
                .tvSeries(null)
                .build();

        return contentMetadataRepo.save(contentMetadata);
    };

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
     * Delete all content related instances from local metadata db and also from S3
     * storage.
     *
     * @param contentId contentId from local db
     * @throws NoSuchElementException when element not found
     * @throws ParseIdException       when of invalid number format defined by api
     * @throws S3Exception            when image wasn't deleted
     */
    @Transactional
    public void removeContent(String contentId) {
        var metadata = contentMetadataRepo
                .findById(contentId)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Не удалось найти запрашиваемый материал по идентификатору."));
        List<String> parsedIds = ProjectStandardUtils.parseIdsFromString(metadata.getPoster().getId());
        posterService.deleteByIds(parsedIds);
        filmingGroupService.deleteById(metadata.getFilmingGroup().getId());
        if (metadata.getTrailer() != null) {
            String id = metadata.getTrailer().getId();
            trailerService.deleteMetadata(id);
            parsedIds = ProjectStandardUtils.parseIdsFromString(id);
            videoUploaderService.deleteTrailerByIds(parsedIds);
        }
        if (metadata.getStandaloneVideoShow() != null) {
            String id = metadata.getStandaloneVideoShow().getId();
            standaloneVideoShowService.deleteMetadata(id);
            parsedIds = ProjectStandardUtils.parseIdsFromString(id);
            videoUploaderService.deleteStandaloneVideoShowByIds(parsedIds);
        }

        contentMetadataRepo.delete(metadata);
    }

    public Optional<ContentMetadata> findById(String id) {
        return contentMetadataRepo.findById(id);
    }

    public List<ContentMetadata> getMetadataByGenre(String name, Integer amount) {
        Genre genre = genreRepo.findByName(name).orElseGet(() -> new Genre("Жанр не указан"));
        return contentMetadataRepo.findByMainGenre(genre, Pageable.ofSize(amount));
    }
}
