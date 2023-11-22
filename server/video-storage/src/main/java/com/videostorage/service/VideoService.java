package com.videostorage.service;

import com.videostorage.model.Video;
import com.videostorage.repo.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class VideoService {
    @Autowired
    private VideoRepo repo;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String saveVideo(String filename, MultipartFile multipartFile) throws IOException {
        Video video = new Video();
        video.setFilename(filename);
        video.setContentType(multipartFile.getContentType());
        repo.save(video);
        String fileId = gridFsTemplate.store(multipartFile.getInputStream(),
                filename, multipartFile.getContentType()).toString();
        return "new video saved: %s with fileId = %s".formatted(video, fileId);
    }

    public ResponseEntity<Resource> getVideo(String fileId) {
        Optional<Video> videoOptional = repo.findById(fileId);
        if (videoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Video video = videoOptional.get();
        GridFsResource gridFsResource = gridFsTemplate.getResource(fileId);
        Resource resource = gridFsResource;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + video.getFilename() + "\"")
                .body(resource);
    }
}
