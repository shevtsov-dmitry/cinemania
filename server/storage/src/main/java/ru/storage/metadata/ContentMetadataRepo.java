package ru.storage.metadata;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentMetadataRepo extends JpaRepository<ContentMetadata, Long> {

    @Query("SELECT e FROM ContentMetadata e ORDER BY e.createdAt DESC")
    List<ContentMetadata> findRecentlyAdded(Pageable requestedAmountRestriction);

    List<ContentMetadata> getByTitle(String title);

    Optional<ContentMetadata> findContentMetadataById(Long id);
}
