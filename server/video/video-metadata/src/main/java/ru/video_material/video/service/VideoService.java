package ru.video_material.video.service;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.video.model.Video;
import ru.video_material.video.model.VideoMetadata;
import ru.video_material.video.repo.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static java.lang.StringTemplate.STR;

@Service
public class VideoService {

    private final VideoRepo repo;
    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public VideoService(VideoRepo repo, GridFsTemplate gridFsTemplate) {
        this.repo = repo;
        this.gridFsTemplate = gridFsTemplate;
    }


    public ResponseEntity<String> save(VideoMetadata videoMetadata) {
        videoMetadata = repo.save(videoMetadata);
        return ResponseEntity.ok(videoMetadata.getId().toString());
    }

    public ResponseEntity<String> deleteById(Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().body(STR."Deletion failed. Entity with id \{id} not found.");
        }
        repo.deleteById(id);
        return ResponseEntity.ok(id.toString());
    }

    public String saveVideo(String title, MultipartFile file) throws IOException {
        Video video = new Video();
        video.setTitle(title);
        video.setContentType(file.getContentType());
        repo.save(video);
        gridFsTemplate.store(file.getInputStream(), title, file.getContentType());
        return "new video saved: %s".formatted(video);
    }

    public String deleteVideo(@PathVariable String title) {
        if (!repo.existsByTitle(title)) {
            return "impossible to delete video.";
        }
        Query.query(Criteria.where("filename").is(title));
        return STR."video \{title} has been deleted successfully.";
    }

}
