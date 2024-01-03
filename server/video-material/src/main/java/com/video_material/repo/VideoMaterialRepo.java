package com.video_material.repo;

import com.mongodb.client.result.DeleteResult;
import com.video_material.model.VideoMaterial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;

public interface VideoMaterialRepo extends JpaRepository<VideoMaterial, Long> {
}
