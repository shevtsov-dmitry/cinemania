package ru.video_material.controller;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.ContentMetadata;
import ru.video_material.service.VideoService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@RestController
@RequestMapping("/videos")
public class VideoController {
    private final VideoService service;

    @Autowired
    public VideoController(VideoService service) {
        this.service = service;
    }

    // ### VIDEO
    @PostMapping("/upload")
    public ResponseEntity<String> saveVideo(@RequestBody MultipartFile file) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        try {
            final String videoId = service.saveVideo(file);
            return new ResponseEntity<>(videoId, httpHeaders, HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(e.getMessage(), httpHeaders, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>("Couldn't read video file.", httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/byId/{id}")
    public ResponseEntity<byte[]> downloadVideoById(@PathVariable String id){
        try {
            return ResponseEntity.ok(service.downloadVideoById(id));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("The file's not found.".getBytes());
        }
    }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.deleteVideoById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // ### METADATA
    @PostMapping("/save/metadata")
    public ResponseEntity<String> saveMetadata(@RequestBody ContentMetadata contentMetadata) {
        return service.saveMetadata(contentMetadata);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Required request body is missing or invalid.");
    }

    @DeleteMapping("/delete/metadata/byId/{id}")
    public ResponseEntity<String> deleteMetadataById(@PathVariable String id) {
        return service.deleteMetadataById(id);
    }

    @GetMapping("/get/metadata/byTitle/{title}")
    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(@PathVariable String title) {
        return service.getMetadataByTitle(title);
    }

    @GetMapping("/get/metadata/byId/{id}")
    public ResponseEntity<ContentMetadata> getMetadataById(@PathVariable String id){
        return service.getMetadataById(id);
    }

}
