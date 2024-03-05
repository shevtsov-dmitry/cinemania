package ru.video_material.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.video_material.model.ContentMetadata;

import java.util.List;

@Repository
public interface MetadataRepo extends MongoRepository<ContentMetadata, String> {
    ContentMetadata getById(String id);
    ContentMetadata getByPosterId(String posterId);
    List<ContentMetadata> getByTitle(String title);

}
