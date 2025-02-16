package ru.storage.content_metadata.video.episode;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EpisodeRepo extends MongoRepository<Episode, String> {}
