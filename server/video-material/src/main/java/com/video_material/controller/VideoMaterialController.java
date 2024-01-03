package com.video_material.controller;

import com.video_material.model.VideoMaterial;
import com.video_material.repo.VideoMaterialRepo;

import java.util.List;

import com.video_material.service.VideoMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video-materials")
public class VideoMaterialController {

    private final VideoMaterialService service;

    @Autowired
    public VideoMaterialController(VideoMaterialService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody VideoMaterial videoMaterial) {
        return service.save(videoMaterial);
    }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return service.deleteById(id);
    }


}
