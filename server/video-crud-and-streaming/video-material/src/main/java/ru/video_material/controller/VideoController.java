package ru.video_material.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.ContentMetadata;
import ru.video_material.service.VideoService;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController {
    private final VideoService service;

    @Autowired
    public VideoController(VideoService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> saveVideo(@RequestBody MultipartFile file) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        try {
            final String videoId = service.uploadVideo(file);
            return new ResponseEntity<>(videoId, httpHeaders, HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(e.getMessage(), httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/save/metadata")
    public ResponseEntity<String> saveMetadata(@RequestBody ContentMetadata contentMetadata) {
        return service.saveMetadata(contentMetadata);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Required request body is missing or invalid.");
    }

    @DeleteMapping("/delete/metadata/byId/{id}")
    public ResponseEntity<String> deleteMetadataById(@PathVariable String id) {
        return service.deleteMetadataById(id);
    }

    @GetMapping("/get/metadata/byTitle/{title}")
    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(@PathVariable String title) {
        return service.getMetadataByTitle(title);
    }

    @GetMapping("/get/metadata/byId/{id}")
    public ResponseEntity<ContentMetadata> getMetadataById(@PathVariable String id) {
        return service.getMetadataById(id);
    }

}
