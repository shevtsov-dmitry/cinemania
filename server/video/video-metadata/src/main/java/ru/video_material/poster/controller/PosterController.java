package ru.video_material.poster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/video-materials/posters")
public class PosterController {

    private final PosterService service;

    @Autowired
    public PosterController(PosterService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> saveImagePoster(@RequestParam MultipartFile file) throws IOException {
        return service.save(file);
    }

    @GetMapping(value = "/get/byId/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPosterById(@PathVariable String id) {
        return service.getById(id);
    }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> deletePosterById(@PathVariable String id){
        return service.deleteById(id);
    }

}
