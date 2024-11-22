package ru.storage.metadata;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataRepo extends JpaRepository<Content, Long> {

    @Query("SELECT e FROM Content e ORDER BY e.createdAt DESC")
    List<Content> findRecentlyAdded(Pageable requestedAmountRestriction);

    List<Content> getByTitle(String title);

}
