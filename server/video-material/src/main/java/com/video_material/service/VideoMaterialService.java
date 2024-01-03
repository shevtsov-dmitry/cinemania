package com.video_material.service;

import com.google.gson.Gson;
import com.video_material.model.VideoMaterial;
import com.video_material.repo.VideoMaterialRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

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
}
