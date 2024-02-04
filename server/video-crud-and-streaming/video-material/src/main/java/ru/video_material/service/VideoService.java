package ru.video_material.service;

import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.Video;
import ru.video_material.model.VideoMetadata;
import ru.video_material.repo.MetadataRepo;
import ru.video_material.repo.BinaryVideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

    public ResponseEntity<String> saveMetadata(VideoMetadata videoMetadata) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        final String id = metadataRepo.save(videoMetadata).getId();
        return new ResponseEntity<>(id, httpHeaders, HttpStatus.OK);
    }

    public String saveVideo(MultipartFile file) throws IOException, NullPointerException {
        if (file == null) {
            throw new NullPointerException("File is absent.");
        }
        Video video = new Video();
        videoRepo.save(video);
        gridFsTemplate.store(file.getInputStream(), "file", Objects.requireNonNull(file.getContentType()));
        return video.getId();
    }

    public void deleteVideoById(String id) {
        if (!videoRepo.existsById(id)) {
            throw new IllegalArgumentException(STR."Deletion failed. The video with id \{id} doesn't exist in GridFS.");
        }
        videoRepo.deleteById(id);
    }

    public ResponseEntity<List<VideoMetadata>> getMetadataByTitle(String title) {
        List<VideoMetadata> occurrences = metadataRepo.getByTitle(title);
        if (occurrences == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(occurrences);
    }

    public ResponseEntity<VideoMetadata> getMetadataById(String id) {
        VideoMetadata metadata = metadataRepo.getById(id);
        if (metadata == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(metadata);
    }

    public ResponseEntity<String> deleteMetadataById(String id) {
        if (!metadataRepo.existsById(id)) {
            return ResponseEntity.badRequest().body(STR."Couldn't find video with id \{id}.");
        }
        return ResponseEntity.ok("");
    }

}
