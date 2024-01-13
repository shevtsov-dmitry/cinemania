package ru.video_material.video.repo;

import ru.video_material.video.model.VideoMaterial;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoMaterialRepo extends JpaRepository<VideoMaterial, Long> {
    boolean existsByTitle(String title);
}
