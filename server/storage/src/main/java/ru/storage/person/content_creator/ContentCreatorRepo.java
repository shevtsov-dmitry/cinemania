package ru.storage.person.content_creator;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ContentCreatorRepo extends MongoRepository<ContentCreator, String> {
    Optional<ContentCreator> findById(String id);

    List<ContentCreator> findByNameStartingWith(String prefix);

    List<ContentCreator> findBySurnameStartingWith(String prefix);

    List<ContentCreator> findByNameLatinStartingWith(String prefix);

    List<ContentCreator> findBySurnameLatinStartingWith(String prefix);
}
