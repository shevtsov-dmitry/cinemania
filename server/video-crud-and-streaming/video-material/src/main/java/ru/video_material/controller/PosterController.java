package ru.video_material.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.service.PosterService;
import ru.video_material.util.PosterWithMetadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/posters")
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
        PosterWithMetadata data = service.getPosterWithMetadataById(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("image", "jpeg"));
        httpHeaders.set("id", data.getContentId());
        try {
            return new ResponseEntity<>(data.getData(), httpHeaders, HttpStatus.OK);
        } catch (NullPointerException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/get/recent/ids/{limit}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getRecentSavedPosterIds(@PathVariable int limit){
        return ResponseEntity.ok(service.getRecentSavedPosterIds(limit));
    }

    @GetMapping(value = "/get/recent/{limit}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<List<byte[]>>> getRecentlySavedPosters(@PathVariable int limit) {
        return ResponseEntity.ok(service.getRecentlySavedPosters(limit));
    }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> deletePosterById(@PathVariable String id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        return service.deleteById(id) ?
                new ResponseEntity<>(STR."poster image with id \{id} successfully deleted.", httpHeaders, HttpStatus.OK) :
                ResponseEntity.notFound().build();
    }


}
