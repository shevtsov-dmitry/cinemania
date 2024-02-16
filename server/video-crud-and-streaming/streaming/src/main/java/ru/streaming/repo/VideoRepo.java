package ru.streaming.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.streaming.model.Video;

public interface VideoRepo extends MongoRepository<Video, String> {

}
