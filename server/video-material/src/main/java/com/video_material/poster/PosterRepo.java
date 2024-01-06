package com.video_material.poster;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PosterRepo extends MongoRepository<Poster, String> {
    Poster getPosterById(String id);
    long deletePosterById(String id);
}
