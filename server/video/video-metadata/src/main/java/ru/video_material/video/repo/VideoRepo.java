package ru.video_material.video.repo;

import ru.video_material.video.model.VideoMetadata;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepo extends JpaRepository<VideoMetadata, Long> {
    boolean existsByTitle(String title);
}
