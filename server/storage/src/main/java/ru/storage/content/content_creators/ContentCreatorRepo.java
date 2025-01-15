package ru.storage.content.content_creators;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentCreatorRepo extends MongoRepository<ContentCreator, String> {
    
}