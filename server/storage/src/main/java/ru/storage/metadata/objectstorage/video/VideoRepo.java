package ru.storage.metadata.objectstorage.video;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepo extends MongoRepository<Video, String> {

}
