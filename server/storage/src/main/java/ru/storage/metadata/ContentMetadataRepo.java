package ru.storage.metadata;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentMetadataRepo extends MongoRepository<ContentMetadata, String> {

//    @Query("SELECT e FROM ContentMetadata e ORDER BY e.createdAt DESC")
//    List<ContentMetadata> findRecentlyAdded(Pageable requestedAmountRestriction);

//    List<ContentMetadata> getByTitle(String title);

    Optional<ContentMetadata> findContentMetadataById(String id);

    List<ContentMetadata> findByOrderByCreatedAtDesc(Pageable pageable);
}
