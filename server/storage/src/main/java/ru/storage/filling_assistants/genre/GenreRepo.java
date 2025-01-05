package ru.storage.filling_assistants.genre;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GenreRepo extends MongoRepository<Genre, String> {
}
