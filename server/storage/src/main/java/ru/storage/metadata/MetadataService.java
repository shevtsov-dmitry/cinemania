package ru.storage.metadata;

import java.util.NoSuchElementException;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.poster.PosterService;
import ru.storage.metadata.objectstorage.video.Video;
import ru.storage.metadata.objectstorage.video.VideoService;

@Service
public class MetadataService {

    private final MetadataRepo metadataRepo;
    private final VideoService videoService;
    private final PosterService posterService;

    public MetadataService(MetadataRepo metadataRepo, VideoService videoService, PosterService posterService) {
        this.metadataRepo = metadataRepo;
        this.videoService = videoService;
        this.posterService = posterService;
    }

    public void saveMetadata(VideoInfoPartsTuple metadataObjects) {
        Poster poster = metadataObjects.poster();
        poster.setContent(metadataObjects.content());
        posterService.saveMetadata(poster);
        Video video = metadataObjects.video();
        video.setContent(metadataObjects.content());
        videoService.saveMetadata(video);
        Content content = metadataObjects.content();
        content.setVideo(video);
        content.setVideo(video);
        metadataRepo.save(content);
    }

}
