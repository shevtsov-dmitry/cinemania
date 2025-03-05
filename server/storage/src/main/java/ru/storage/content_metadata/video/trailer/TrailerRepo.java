package ru.storage.content_metadata.video.trailer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrailerRepo extends MongoRepository<Trailer, String> {
}
