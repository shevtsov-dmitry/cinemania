package com.filminfopage.controller;

import com.filminfopage.model.VideoMaterial;
import com.filminfopage.repo.VideoMaterialRepo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/film-info")
public class VideoMaterialController {

    private final VideoMaterialRepo repo;

    @Autowired
    public VideoMaterialController(VideoMaterialRepo repo) {
        this.repo = repo;
    }

    @PostMapping("/add")
    public boolean saveData(@RequestBody VideoMaterial videoMaterial) {
        VideoMaterial saved = repo.save(videoMaterial);
        return saved == null;
    }

    @GetMapping("/get/latest-saved")
    public VideoMaterial getLatestSaved() {
        List<VideoMaterial> singleton = repo.getLastSaved();
        if (singleton == null) {
            return new VideoMaterial();
        }
        return singleton.get(0);
    }

    @GetMapping("/get/all")
    public List<VideoMaterial> getAllFilmsInfo() {
        return repo.findAll();
    }


}
