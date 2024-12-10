package ru.storage.metadata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.poster.PosterService;
import ru.storage.metadata.objectstorage.video.Video;
import ru.storage.metadata.objectstorage.video.VideoService;

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
     * @param metadataObjects metadata record of {@link Video}, {@link Poster} and {@link ContentMetadata}
     * @return {@link VideoInfoParts} object
     * @throws NoMetadataRelationException when some of the instances doesn't have field related jpa field
     * @throws IllegalArgumentException    when content type is wrong
     */
    public VideoInfoParts saveMetadata(VideoInfoParts metadataObjects) {
        Video savedVideoMetadata = videoService.saveMetadata(metadataObjects.video());
        Poster savedPosterMetadata = posterService.saveMetadata(metadataObjects.poster());
        ContentMetadata savedContentMetadata = metadataObjects.contentMetadata();
        savedContentMetadata.setVideo(savedVideoMetadata);
        savedContentMetadata.setPoster(savedPosterMetadata);
        savedContentMetadata = contentMetadataRepo.save(savedContentMetadata);
        return new VideoInfoParts(savedContentMetadata, savedVideoMetadata, savedPosterMetadata);
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
