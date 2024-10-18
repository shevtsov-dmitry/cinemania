package ru.storage.objectstorage.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Required request body is missing or invalid.");
    }

}
