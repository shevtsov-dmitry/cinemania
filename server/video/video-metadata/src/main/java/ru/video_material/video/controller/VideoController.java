package ru.video_material.video.controller;

import org.springframework.web.multipart.MultipartFile;
import ru.video_material.video.model.VideoMetadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.video_material.video.service.VideoService;

import java.io.IOException;

@RestController
@RequestMapping("/video-materials")
public class VideoController {

    private final VideoService service;

    @Autowired
    public VideoController(VideoService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody VideoMetadata videoMetadata) {
        return service.save(videoMetadata);
    }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return service.deleteById(id);
    }

    @PostMapping("/upload/one")
    public ResponseEntity<String> saveVideo(@RequestBody MultipartFile file, @RequestParam String title) {
        try {
            String successMessage = service.saveVideo(title, file);
            return ResponseEntity.status(200).body(successMessage);
        } catch (IOException exception) {
            return ResponseEntity.status(500).body("Error uploading video");
        }
    }

    @GetMapping(value = "/delete/one/byTitle/{name}")
    public String deleteVideo(@PathVariable String name) {
        return service.deleteVideo(name);
    }
}
