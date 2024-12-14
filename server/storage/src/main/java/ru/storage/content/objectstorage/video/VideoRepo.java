package ru.storage.content.objectstorage.video;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepo extends MongoRepository<Video, String> {

}
