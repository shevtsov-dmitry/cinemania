package com.video_material.poster.repo;

import com.video_material.poster.model.Poster;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PosterRepo extends MongoRepository<Poster, String> {
    Poster getPosterById(String id);
    long deletePosterById(String id);
}
