package ru.storage.metadata;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface MetadataRepo extends JpaRepository<ContentMetadata, Long> {

    @Query("SELECT e FROM ContentMetadata e ORDER BY e.createdAt DESC")
    List<ContentMetadata> findRecentlyAdded(Pageable requestedAmountRestriction);

    List<ContentMetadata> getByTitle(String title);

}
