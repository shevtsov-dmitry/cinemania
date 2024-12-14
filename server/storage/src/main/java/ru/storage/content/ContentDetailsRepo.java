package ru.storage.content;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentDetailsRepo extends MongoRepository<ContentDetails, String> {

//    @Query("SELECT e FROM ContentMetadata e ORDER BY e.createdAt DESC")
//    List<ContentMetadata> findRecentlyAdded(Pageable requestedAmountRestriction);

//    List<ContentMetadata> getByTitle(String title);

    Optional<ContentDetails> findContentMetadataById(String id);

    List<ContentDetails> findByOrderByCreatedAtDesc(Pageable pageable);
}
