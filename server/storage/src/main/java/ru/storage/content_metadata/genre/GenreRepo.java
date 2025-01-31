package ru.storage.content_metadata.genre;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepo extends MongoRepository<Genre, String> {

    void deleteByName(String name);
}
