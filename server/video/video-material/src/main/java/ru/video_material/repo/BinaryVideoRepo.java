package ru.video_material.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.video_material.model.Video;

@Repository
public interface BinaryVideoRepo extends MongoRepository<Video, String> {
}
