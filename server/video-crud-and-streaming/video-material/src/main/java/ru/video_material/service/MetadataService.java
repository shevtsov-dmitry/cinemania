package ru.video_material.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.video_material.model.ContentMetadata;
import ru.video_material.repo.MetadataRepo;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MetadataService {

    @Autowired
    private MetadataRepo metadataRepo;

    public ResponseEntity<String> saveMetadata(ContentMetadata contentMetadata) {
        contentMetadata.setCreatedAt(LocalDateTime.now());
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        final String id = metadataRepo.save(contentMetadata).getId();
        return new ResponseEntity<>(id, httpHeaders, HttpStatus.OK);
    }

    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(String title) {
        List<ContentMetadata> occurrences = metadataRepo.getByTitle(title);
        if (occurrences == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(occurrences);
    }

    public ResponseEntity<ContentMetadata> getMetadataById(String id) {
        ContentMetadata metadata = metadataRepo.getById(id);
        if (metadata == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(metadata);
    }

    public ResponseEntity<String> deleteMetadataById(String id) {
        if (!metadataRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Couldn't find video with id %s.".formatted(id));
        }
        return ResponseEntity.ok("");
    }

}
