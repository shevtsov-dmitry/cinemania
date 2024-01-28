package ru.video_material.service;

import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.Video;
import ru.video_material.model.VideoMetadata;
import ru.video_material.repo.MetadataRepo;
import ru.video_material.repo.BinaryVideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static java.lang.StringTemplate.STR;

@Service
public class VideoService {

    private final BinaryVideoRepo videoRepo;
    private final MetadataRepo metadataRepo;
    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public VideoService(BinaryVideoRepo videoRepo, MetadataRepo metadataRepo, GridFsTemplate gridFsTemplate) {
        this.videoRepo = videoRepo;
        this.metadataRepo = metadataRepo;
        this.gridFsTemplate = gridFsTemplate;
    }


    public String saveMetadata(VideoMetadata videoMetadata) throws IllegalArgumentException {
        if (videoMetadata == null) {
            throw new IllegalArgumentException("Video metadata is absent.");
        }
        return metadataRepo.save(videoMetadata).getId();
    }

    public String saveVideo(MultipartFile file) throws IOException, NullPointerException {
        if (file == null)  {
            throw new NullPointerException("File is absent.");
        }
        Video video = new Video();
        videoRepo.save(video);
        gridFsTemplate.store(file.getInputStream(), Objects.requireNonNull(file.getContentType()));
        return video.getId();
    }

    public String deleteVideoMetadataById(String id) throws IllegalArgumentException {
        var metadata = metadataRepo.getById(id);
        if(metadata == null) {
            throw new IllegalArgumentException(STR."Deletion failed. Entity with id \{id} not found.");
        }
        return metadata.getVideoId();
    }

    // ? Maybe I will delete this in the future. Metadata should not exist without binary video and otherwise.
    public void deleteVideoById(String id) {
        if (!videoRepo.existsById(id)) {
            throw new IllegalArgumentException(STR."Deletion failed. The video with id \{id} doesn't exist in GridFS.");
        }
        videoRepo.deleteById(id);
    }

}
