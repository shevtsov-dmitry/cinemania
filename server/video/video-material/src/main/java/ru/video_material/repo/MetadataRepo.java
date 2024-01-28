package ru.video_material.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.video_material.model.VideoMetadata;

import java.util.List;

@Repository
public interface MetadataRepo extends MongoRepository<VideoMetadata, Long> {
    VideoMetadata getById(String id);
    List<VideoMetadata> getByTitle(String title);
}
