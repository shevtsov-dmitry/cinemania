package com.videostorage.service;

import com.videostorage.model.Video;
import com.videostorage.repo.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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


    public ResponseEntity<InputStreamResource> getVideo(String name) throws IllegalStateException, IOException {
        GridFsResource videoResource = gridFsTemplate.getResource(name);
        InputStream videoStream = videoResource.getInputStream();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(videoResource.getContentType()))
                .body(new InputStreamResource(videoStream));
    }
}
