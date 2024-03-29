package ru.video_material.repo;

import org.springframework.stereotype.Repository;
import ru.video_material.model.Poster;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface PosterRepo extends MongoRepository<Poster, String> {
    Poster getPosterById(String id);
    long deletePosterById(String id);
}
