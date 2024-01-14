package ru.streaming.repo;

import ru.video_material.video.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepo extends MongoRepository<Video, String> {

}
