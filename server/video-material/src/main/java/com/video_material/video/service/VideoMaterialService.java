package com.video_material.video.service;

import com.video_material.video.model.VideoMaterial;
import com.video_material.video.repo.VideoMaterialRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VideoMaterialService {
    private final VideoMaterialRepo repo;

    @Autowired
    public VideoMaterialService(VideoMaterialRepo repo) {
        this.repo = repo;
    }

    public ResponseEntity<String> save(VideoMaterial videoMaterial) {
        videoMaterial = repo.save(videoMaterial);
        return ResponseEntity.ok(videoMaterial.getId().toString());
    }

    public ResponseEntity<String> deleteById(Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().body(STR."Deletion failed. Entity with id \{id} not found.");
        }
        repo.deleteById(id);
        return ResponseEntity.ok(id.toString());
    }
}
