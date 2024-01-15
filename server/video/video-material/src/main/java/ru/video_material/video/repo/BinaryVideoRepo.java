package ru.video_material.video.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.video_material.video.model.Video;

@Repository
public interface BinaryVideoRepo extends MongoRepository<Video, Long> {
    boolean existsByTitle(String title);
}
