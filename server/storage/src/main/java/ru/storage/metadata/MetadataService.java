package ru.storage.metadata;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

    @Autowired
    private MetadataRepo metadataRepo;

    public String saveMetadata(ContentMetadata contentMetadata) {
        return metadataRepo.save(contentMetadata).getId();
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
