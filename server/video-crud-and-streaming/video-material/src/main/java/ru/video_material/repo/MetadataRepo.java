package ru.video_material.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.video_material.model.ContentMetadata;

import java.util.List;

@Repository
public interface MetadataRepo extends MongoRepository<ContentMetadata, String> {

    @Query(value = "{}", sort = "{createdAt: -1}")
    List<ContentMetadata> findTopNByOrderByCreatedAtDesc(Pageable requestedAmountRestriction);

    ContentMetadata getById(String id);

    ContentMetadata getByPosterId(String posterId);

    List<ContentMetadata> getByTitle(String title);

}
