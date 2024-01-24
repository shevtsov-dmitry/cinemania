package ru.video_material.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.video_material.model.VideoMetadata;

@Repository
public interface VideoMetadataRepo extends MongoRepository<VideoMetadata, Long> {
    boolean existsByTitle(String title);
}
