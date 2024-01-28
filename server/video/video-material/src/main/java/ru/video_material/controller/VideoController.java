package ru.video_material.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.VideoMetadata;
import ru.video_material.service.VideoService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService service;

    @Autowired
    public VideoController(VideoService service) {
        this.service = service;
    }

    @PostMapping("/save-metadata")
    public ResponseEntity<String> saveMetadata(@RequestBody VideoMetadata videoMetadata) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        try {
            final String id = service.saveMetadata(videoMetadata);
            return new ResponseEntity<>(id, httpHeaders, HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(nullPointerException.getMessage(), httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> saveVideo(@RequestBody MultipartFile file) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        try {
            final String videoId = service.saveVideo(file);
            return new ResponseEntity<>(videoId, httpHeaders, HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(e.getMessage(), httpHeaders, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>("Couldn't read video file.", httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        try {
            String videoId = service.deleteVideoMetadataById(id);
            service.deleteVideoById(videoId);
            return ResponseEntity.ok(id);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

//    @GetMapping("/get/byId")

}
