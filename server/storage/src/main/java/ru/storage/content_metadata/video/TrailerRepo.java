package ru.storage.content_metadata.video;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrailerRepo extends MongoRepository<Trailer, String> {}
