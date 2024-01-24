package ru.video_material.controller;

import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.VideoMetadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.video_material.service.VideoService;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService service;

    @Autowired
    public VideoController(VideoService service) {
        this.service = service;
    }

    @PostMapping("/save-metadata")
    public ResponseEntity<String> save(@RequestBody VideoMetadata videoMetadata) {
        return service.saveVideoMetadata(videoMetadata);
    }

    @PostMapping("/upload/one")
    public ResponseEntity<String> saveVideo(@RequestBody MultipartFile file, @RequestParam String title) {
        return service.saveVideo(title, file);
    }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        return service.deleteById(id);
    }

    @GetMapping(value = "/delete/one/byTitle/{name}")
    public String deleteVideo(@PathVariable String name) {
        return service.deleteVideo(name);
    }
}
