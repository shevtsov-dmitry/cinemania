package com.videostorage.controller;

import com.videostorage.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

    @PostMapping("/upload/one")
    public ResponseEntity<String> saveVideo(@RequestParam String name, @RequestParam MultipartFile file) {
        try {
            String successMessage = service.saveVideo(name, file);
            return ResponseEntity.status(200).body(successMessage);
        } catch (IOException exception) {
            exception.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading video");
        }
    }

    @GetMapping(value = "/get/one/byName/{name}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> sendVideoResponse(@PathVariable String name) throws IOException {
        return service.getVideo(name);
    }
}
