package ru.storage.metadata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.storage.metadata.objectstorage.exceptions.NoMetadataRelationException;
import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.poster.PosterService;
import ru.storage.metadata.objectstorage.video.Video;
import ru.storage.metadata.objectstorage.video.VideoService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MetadataService {

    private final ContentMetadataRepo contentMetadataRepo;
    private final VideoService videoService;
    private final PosterService posterService;

    public MetadataService(ContentMetadataRepo contentMetadataRepo, VideoService videoService, PosterService posterService) {
        this.contentMetadataRepo = contentMetadataRepo;
        this.videoService = videoService;
        this.posterService = posterService;
    }

    /**
     * @param metadataObjects metadata record of {@code Video}, {@code Poster} and {@code ContentMetadata}
     * @return {@code id} of saved content metadata
     * @throws NoMetadataRelationException when some of the instances doesn't have field related jpa field
     * @throws IllegalArgumentException    when content type is wrong
     */
    public long saveMetadata(VideoInfoParts metadataObjects) {
        Poster poster = metadataObjects.poster();
        poster.setContentMetadata(metadataObjects.contentMetadata());
        posterService.saveMetadata(poster);

        Video video = metadataObjects.video();
        video.setContentMetadata(metadataObjects.contentMetadata());
        videoService.saveMetadata(video);

        ContentMetadata contentMetadata = metadataObjects.contentMetadata();
        contentMetadata.setPoster(poster);
        contentMetadata.setVideo(video);
        return contentMetadataRepo.save(contentMetadata).getId();
    }

    /**
     * Retrieve recently added video info parts bundle
     *
     * @param amount how many instances need to be searched
     * @return recently added {@code VideoInfoParts} bundle
     */
    public List<VideoInfoParts> getRecentlyAdded(int amount) {
        List<ContentMetadata> recentlyAdded = contentMetadataRepo.findRecentlyAdded(Pageable.ofSize(amount));
        return recentlyAdded.stream()
                .map(contentMetadata -> {
                    long id = contentMetadata.getId();
                    return new VideoInfoParts(
                            contentMetadata,
                            videoService.findByContentMetadataId(id).orElse(new Video()),
                            posterService.findByContentMetadataId(id).orElse(new Poster())
                    );
                }).toList();
    }

    // public List<ContentMetadata> queryMetadataRepoForIds(int amount) {
    // final Pageable requestedAmountRestriction = PageRequest.of(0, amount);
    // return metadataRepo.findRecentlyAdded(requestedAmountRestriction).stream()
    // .map(ContentMetadata::getPosterId)
    // .toList();
    // }
    //
    // public List<Map<String, byte[]>> getRecentlySavedPosters(int amount) {
    // List<String> recentSavedPosterIds = queryMetadataRepoForIds(amount);
    // List<Map<String, byte[]>> imagesAndMetadata = new ArrayList<>(amount);
    //
    // for (String id : recentSavedPosterIds) {
    // Map<String, byte[]> data = new HashMap<>();
    //
    // Poster poster = posterRepo.findById(id);
    // final ContentMetadata metadata = metadataRepo.getByPosterId(poster.getId());
    // data.put("metadataId", metadata.getId().getBytes());
    // data.put("title", metadata.getTitle().getBytes());
    // data.put("releaseDate", metadata.getReleaseDate().getBytes());
    // data.put("country", metadata.getCountry().getBytes());
    // data.put("mainGenre", metadata.getMainGenre().getBytes());
    // data.put("subGenres", metadata.getSubGenres().toString().replace("[",
    // "").replace("]", "").getBytes());
    // data.put("age", metadata.getAge().toString().getBytes());
    // data.put("rating", String.valueOf(metadata.getRating()).getBytes());
    // data.put("poster", poster.getImage().getData());
    // data.put("videoId", metadata.getVideoId().getBytes());
    //
    // imagesAndMetadata.add(data);
    // }
    // return imagesAndMetadata;
    // }

}
