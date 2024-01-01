package com.filminfopage.controller;

import com.filminfopage.service.PosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/film-info/posters")
public class PosterController {

    private final PosterService service;

    @Autowired
    public PosterController(PosterService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> saveImagePoster(@RequestParam String title, @RequestParam MultipartFile file) throws IOException {
        return service.save(title, file);
    }

    @GetMapping(value = "/get/byId/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPosterById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping(value = "/get/byTitle/{title}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPosterByTitle(@PathVariable String title) {
        return service.getByTitle(title);
    }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> deletePosterById(@PathVariable String id){
        return service.deleteById(id);
    }

    @DeleteMapping("/delete/byTitle/{title}")
    public ResponseEntity<String> deletePosterByTitle(@PathVariable String title){
        return service.deleteByTitle(title);
    }

}
