package ru.storage.person.content_creator;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ContentCreatorRepo extends MongoRepository<ContentCreator, String> {
    Optional<ContentCreator> findContentCreatorById(String id);
    List<ContentCreator> findByFullnameStartingWith(String prefix);

}