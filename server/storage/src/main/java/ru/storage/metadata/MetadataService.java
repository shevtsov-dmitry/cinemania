package ru.storage.metadata;

import org.springframework.stereotype.Service;
import ru.storage.metadata.objectstorage.exceptions.NoMetadataRelationException;
import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.poster.PosterService;
import ru.storage.metadata.objectstorage.video.Video;
import ru.storage.metadata.objectstorage.video.VideoService;

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
    public long saveMetadata(VideoInfoPartsTuple metadataObjects) {
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

}
