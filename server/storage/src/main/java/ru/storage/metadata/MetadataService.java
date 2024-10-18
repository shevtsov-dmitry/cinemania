package ru.storage.metadata;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

    private final MetadataRepo repo;

    public MetadataService(MetadataRepo repo) {
        this.repo = repo;
    }

    public Optional<ContentMetadata> saveMetadata(ContentMetadata contentMetadata) {
        return Optional.of(repo.save(contentMetadata));
    }

    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(String title) {
        List<ContentMetadata> occurrences = repo.getByTitle(title);
        if (occurrences == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(occurrences);
    }

    public ResponseEntity<ContentMetadata> getMetadataById(Long id) {
        final var metadata = repo.findById(id);
        return metadata.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    public ResponseEntity<String> deleteMetadataById(Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().body("Couldn't find video with id %s.".formatted(id));
        }
        return ResponseEntity.ok("");
    }

}
