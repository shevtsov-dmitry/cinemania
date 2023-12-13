package com.videostorage.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.videostorage.model.Video;
import com.videostorage.repo.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class VideoService {
    @Autowired
    private VideoRepo repo;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations operations;

    public String saveVideo(String filename, MultipartFile videoFile) throws IOException {
        Video video = new Video();
        video.setTitle(filename);
        video.setContentType(videoFile.getContentType());
        repo.save(video);
        gridFsTemplate.store(videoFile.getInputStream(), filename, videoFile.getContentType()).toString();
        return "new video saved: %s".formatted(video);
    }

    public Video getVideo(String title) throws IllegalStateException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("filename").is(title)));
        Video video = new Video();
        video.setTitle(file.getFilename());
        video.setContentType(file.getMetadata().toJson());
        video.setStream(operations.getResource(file).getInputStream());
        return video;
    }

    public String deleteVideo(@PathVariable String title) {
        if (!repo.existsByTitle(title)) {
            return "impossible to delete video.";
        }
        Query queryDeleteVideo = Query.query(Criteria.where("filename").is(title));
//            gridFsTemplate.delete(queryDeleteVideo);
        return STR."video \{title} has been deleted successfully.";
//        }
    }
}
