package ru.storage.poster;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface PosterRepo extends MongoRepository<Poster, String> {

    Poster getPosterById(String id);

    long deletePosterById(String id);
}
