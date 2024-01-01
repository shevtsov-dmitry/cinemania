package com.video_material.controller;

import com.video_material.model.VideoMaterial;
import com.video_material.repo.VideoMaterialRepo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video-materials")
public class VideoMaterialController {
    private final VideoMaterialRepo repo;

    @Autowired
    public VideoMaterialController(VideoMaterialRepo repo) {
        this.repo = repo;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody ) {

    }


}
