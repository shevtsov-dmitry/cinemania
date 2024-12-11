package ru.storage.metadata;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.poster.PosterService;
import ru.storage.metadata.objectstorage.video.Video;
import ru.storage.metadata.objectstorage.video.VideoService;

import java.util.List;

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
     * @param metadataObjects {@link VideoInfoParts} metadata record of {@link Video}, {@link Poster} and {@link ContentMetadata}
     * @return {@link VideoInfoParts} object
     * @throws IllegalArgumentException when content type is wrong
     */
    public ContentMetadata saveMetadata(VideoInfoParts metadataObjects) {
        Video savedVideoMetadata = videoService.saveMetadata(metadataObjects.video());
        Poster savedPosterMetadata = posterService.saveMetadata(metadataObjects.poster());
        ContentMetadata contentDetails = metadataObjects.contentMetadata();
        if (contentDetails == null)
            throw new IllegalArgumentException("Необходимые сведения о загружаемом видео-проекте отсутствуют");

        contentDetails.setVideo(savedVideoMetadata);
        contentDetails.setPoster(savedPosterMetadata);
        return contentMetadataRepo.save(contentDetails);
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

//    /**
//     * Retrieve recently added video info parts bundle
//     *
//     * @param amount how many instances need to be searched
//     * @return recently added {@code VideoInfoParts} bundle
//     */
//    public List<VideoInfoParts> getRecentlyAdded(int amount) {
//        List<ContentMetadata> recentlyAdded = contentMetadataRepo.findRecentlyAdded(Pageable.ofSize(amount));
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
