package ru.video_material.video.controller;

import ru.video_material.video.model.VideoMaterial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video-materials")
public class VideoMaterialController {

    @Autowired
    private final VideoService service;

    @Autowired
    public VideoMaterialController(VideoMaterialService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody VideoMaterial videoMaterial) {
        return service.save(videoMaterial);
    }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return service.deleteById(id);
    }

    @PostMapping("/upload/one")
    public ResponseEntity<String> saveVideo(@RequestBody MultipartFile file, @RequestParam String title) {
        try {
            log.info("title: {}", title);
            String successMessage = service.saveVideo(title, file);
            return ResponseEntity.status(200).body(successMessage);
        } catch (IOException exception) {
            exception.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading video");
        }
    }

    @GetMapping(value = "/delete/one/byTitle/{name}")
    public String deleteVideo(@PathVariable String name) {
        return service.deleteVideo(name);
    }
}
