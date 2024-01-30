<<<<<<<< HEAD:server/video/video-material/src/main/java/ru/video_material/repo/PosterRepo.java
package ru.video_material.repo;

import org.springframework.stereotype.Repository;
import ru.video_material.model.Poster;
========
package com.video_material.poster;

>>>>>>>> add-film-form-fix:server/video-material/src/main/java/com/video_material/poster/PosterRepo.java
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface PosterRepo extends MongoRepository<Poster, String> {
    Poster getPosterById(String id);
    long deletePosterById(String id);
}
