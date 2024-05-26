package ru.video_material.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.video_material.model.ContentMetadata;
import ru.video_material.service.MetadataService;

import java.util.List;

@RestController
@RequestMapping("/videos/metadata")
public class MetadataController {

    @Autowired
    private MetadataService service;

    @PostMapping("/save")
    public ResponseEntity<String> saveMetadata(@RequestBody ContentMetadata contentMetadata) {
        return service.saveMetadata(contentMetadata);
    }

    @GetMapping(value = "/get/metadata/byTitle/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(@PathVariable String title) {
        return service.getMetadataByTitle(title);
    }

    @GetMapping(value = "/get/metadata/byId/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContentMetadata> getMetadataById(@PathVariable String id) {
        return service.getMetadataById(id);
    }

    @DeleteMapping("/delete/metadata/byId/{id}")
    public ResponseEntity<String> deleteMetadataById(@PathVariable String id) {
        return service.deleteMetadataById(id);
    }

}
