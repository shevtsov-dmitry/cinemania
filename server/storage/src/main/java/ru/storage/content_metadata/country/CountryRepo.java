package ru.storage.content_metadata.country;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepo extends MongoRepository<Country, String> {
    void deleteByName(String name);
}
