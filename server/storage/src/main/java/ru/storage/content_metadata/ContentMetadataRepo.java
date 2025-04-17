package ru.storage.content_metadata;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ru.storage.content_metadata.genre.Genre;

import java.util.List;

@Repository
public interface ContentMetadataRepo extends MongoRepository<ContentMetadata, String> {

    List<ContentMetadata> findByOrderByCreatedAtDesc(Pageable pageable);

    List<ContentMetadata> findByMainGenre(Genre genre, Pageable pageable);

    // find by creator
    // List<ContentMetadata> findByOrderByCreator(creator);

}
