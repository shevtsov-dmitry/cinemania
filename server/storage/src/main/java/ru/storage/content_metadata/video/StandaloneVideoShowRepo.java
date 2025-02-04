package ru.storage.content_metadata.video;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StandaloneVideoShowRepo extends MongoRepository<StandaloneVideoShow, String> {

}
