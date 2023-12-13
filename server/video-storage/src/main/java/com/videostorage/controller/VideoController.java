package com.videostorage.controller;

import com.videostorage.model.Video;
import com.videostorage.service.VideoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
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

    @GetMapping(value = "/get/one/byTitle/{title}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Video sendVideoResponse(@PathVariable String title) throws IOException {
        return service.getVideo(title);
    }

    @GetMapping(value =  "/delete/one/byTitle/{name}")
    public String deleteVideo(@PathVariable String name){
        return service.deleteVideo(name);
    }

    @GetMapping("/stream/{title}")
    public void streamVideo(@PathVariable String title, HttpServletResponse response) throws Exception {
        Video video = service.getVideo(title);
        FileCopyUtils.copy(video.getStream(), response.getOutputStream());
    }
}
