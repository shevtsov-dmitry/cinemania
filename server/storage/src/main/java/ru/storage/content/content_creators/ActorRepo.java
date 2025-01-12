package ru.storage.content.content_creators;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepo extends MongoRepository<Actor, String> {}
