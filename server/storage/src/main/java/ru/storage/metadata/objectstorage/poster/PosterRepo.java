package ru.storage.metadata.objectstorage.poster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.storage.metadata.ContentMetadata;

import java.util.Optional;

@Repository
public interface PosterRepo extends JpaRepository<Poster, Long> {

    @Modifying
    @Query("update Poster p set p.contentMetadata = :contentMetadata, p.filename =:filename, p.contentType = :contentType")
    void updatePosterByContentMetadata(ContentMetadata contentMetadata, String filename, String contentType);

    @Modifying
    @Query("delete from Poster p where p.contentMetadata.id = :metadataId")
    void deleteByContentMetadataId(Long metadataId);

    Optional<Poster> findByContentMetadataId(Long contentId);

}
