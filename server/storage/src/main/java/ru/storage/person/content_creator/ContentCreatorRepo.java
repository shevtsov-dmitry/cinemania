package ru.storage.person.content_creator;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentCreatorRepo extends MongoRepository<ContentCreator, String> {
}