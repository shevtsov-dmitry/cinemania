package ru.video_material.video.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.video_material.video.model.VideoMetadata;

public interface VideoMetadataPostgresRepo extends JpaRepository<VideoMetadata, Long> {

}
