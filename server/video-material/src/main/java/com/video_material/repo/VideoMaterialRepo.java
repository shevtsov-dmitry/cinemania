package com.video_material.repo;

import com.video_material.model.VideoMaterial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VideoMaterialRepo extends JpaRepository<VideoMaterial, Long> {
}
