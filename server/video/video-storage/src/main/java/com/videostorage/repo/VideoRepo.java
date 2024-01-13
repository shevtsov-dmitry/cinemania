package com.videostorage.repo;

import com.videostorage.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepo extends MongoRepository<Video, String> {

}
