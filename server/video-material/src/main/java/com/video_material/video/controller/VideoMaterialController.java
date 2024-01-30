package com.video_material.video.controller;

import com.video_material.video.model.VideoMaterial;

import com.video_material.video.service.VideoMaterialService;
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
