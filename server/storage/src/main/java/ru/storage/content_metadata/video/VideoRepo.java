package ru.storage.content_metadata.video;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepo extends MongoRepository<StandaloneVideoShow, String> {

}
