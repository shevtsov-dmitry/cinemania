package ru.video_material.video.repo;

import ru.video_material.video.model.Video;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoMongoRepo extends JpaRepository<Video, Long> {
    boolean existsByTitle(String title);
}
