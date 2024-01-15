package ru.video_material.video.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.video_material.video.model.VideoMetadata;

@Repository
public interface VideoMetadataRepo extends MongoRepository<VideoMetadata, Long> {

}
