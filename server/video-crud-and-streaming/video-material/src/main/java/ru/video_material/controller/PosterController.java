package ru.video_material.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.service.PosterService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/videos/posters")
public class PosterController {

    private final PosterService service;

    @Autowired
    public PosterController(PosterService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> savePoster(@RequestParam MultipartFile file) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        try {
            String savedPosterId = service.save(file);
            return new ResponseEntity<>(savedPosterId, httpHeaders, HttpStatus.OK);
        } catch (NullPointerException | IOException ex) {
            return new ResponseEntity<>(
                    "Impossible to read video file.", httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(
                    "Couldn't save video. Video content is empty.", httpHeaders, HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping(value = "/get/byId/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPosterById(@PathVariable String id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("image", "jpeg"));
        try {
            return new ResponseEntity<>(service.getById(id), httpHeaders, HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("Poster Not Found.".getBytes(), httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> deletePosterById(@PathVariable String id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        return service.deleteById(id) ?
                new ResponseEntity<>(STR."video file with id \{id} successfully deleted.", httpHeaders, HttpStatus.OK) :
                ResponseEntity.notFound().build();
    }

}
