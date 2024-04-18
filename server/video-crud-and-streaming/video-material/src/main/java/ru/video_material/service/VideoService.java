package ru.video_material.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.StaticValues;
import ru.video_material.model.ContentMetadata;
import ru.video_material.repo.MetadataRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VideoService {

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

    public String uploadVideo(MultipartFile file) {
        final String id = generateId();
        String fileName = file.getOriginalFilename();
        Path uploadDirectory = Paths.get(StaticValues.VIDEO_STORAGE_PATH);
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadDirectory.resolve(id.concat(".mp4"));
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: %s".formatted(fileName), e);
        }
    }

    private static String generateId() {
        final String base64Digits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int idLength = 40;
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < idLength; i++) {
            int position = (int) (Math.random() * base64Digits.length());
            id.append(base64Digits.charAt(position));
        }
        return id.toString();
    }
}
