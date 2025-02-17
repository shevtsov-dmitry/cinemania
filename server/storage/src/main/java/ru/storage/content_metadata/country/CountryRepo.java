package ru.storage.content_metadata.country;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.storage.content_metadata.genre.Genre;

import java.util.Optional;

@Repository
public interface CountryRepo extends MongoRepository<Country, String> {
    void deleteByName(String name);

    Optional<Country> findByName(String name);
}
