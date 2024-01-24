package ru.video_material.service;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.Video;
import ru.video_material.model.VideoMetadata;
import ru.video_material.repo.VideoMetadataRepo;
import ru.video_material.repo.BinaryVideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static java.lang.StringTemplate.STR;

@Service
public class VideoService {

    private final BinaryVideoRepo videoRepo;
    private final VideoMetadataRepo metadataRepo;
    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public VideoService(BinaryVideoRepo videoRepo, VideoMetadataRepo metadataRepo, GridFsTemplate gridFsTemplate) {
        this.videoRepo = videoRepo;
        this.metadataRepo = metadataRepo;
        this.gridFsTemplate = gridFsTemplate;
    }


    public ResponseEntity<String> saveVideoMetadata(VideoMetadata videoMetadata) {
        videoMetadata = metadataRepo.save(videoMetadata);
        return ResponseEntity.ok(videoMetadata.getId());
    }

    public ResponseEntity<String> saveVideo(String title, MultipartFile file){
        try {
            Video video = new Video();
            videoRepo.save(video);
            gridFsTemplate.store(file.getInputStream(), title, file.getContentType());
            return ResponseEntity.ok(video.getId());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Couldn't save the video");
        }
    }

    public ResponseEntity<String> deleteById(String id) {
        if (!videoRepo.existsById(id)) {
            return ResponseEntity.badRequest().body(STR."Deletion failed. Entity with id \{id} not found.");
        }
        videoRepo.deleteById(id);
        return ResponseEntity.ok(id);
    }

    public String deleteVideo(@PathVariable String title) {
        if (!metadataRepo.existsByTitle(title)) {
            return "impossible to delete video.";
        }
        Query.query(Criteria.where("filename").is(title));
        return STR."video \{title} has been deleted successfully.";
    }

}
