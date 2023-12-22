package com.videostorage.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.videostorage.model.Video;
import com.videostorage.repo.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class VideoService {
    @Autowired
    private VideoRepo repo;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations operations;
    @Autowired
    private ResourceLoader resourceLoader;

    public String saveVideo(String title, MultipartFile file) throws IOException {
        Video video = new Video();
        video.setTitle(title);
        video.setContentType(file.getContentType());
        repo.save(video);
        gridFsTemplate.store(file.getInputStream(), title, file.getContentType());
        return "new video saved: %s".formatted(video);
    }

    public Mono<Resource> getVideo(String title) throws IllegalStateException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("filename").is(title)));
        if(file == null) {
            return Mono.error(new FileNotFoundException());
        }
        var outputStream = new ByteArrayOutputStream();
        operations.getResource(file).getInputStream().transferTo(outputStream);
        byte[]  binaryContent = outputStream.toByteArray();
        var resource = new ByteArrayResource(binaryContent);
        return Mono.just(resource);
    }


    public String deleteVideo(@PathVariable String title) {
        if (!repo.existsByTitle(title)) {
            return "impossible to delete video.";
        }
        Query queryDeleteVideo = Query.query(Criteria.where("filename").is(title));
        return STR."video \{title} has been deleted successfully.";
    }
}
