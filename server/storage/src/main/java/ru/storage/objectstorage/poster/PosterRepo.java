package ru.storage.objectstorage.poster;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.web.bind.annotation.ModelAttribute;
import ru.storage.metadata.ContentMetadata;

@Repository
public interface PosterRepo extends JpaRepository<Poster, Long> {

    @Modifying
    @Query("delete from Poster e where e.contentMetadata.id = :contentMetadataId")
    void deleteByContentMetadataId(Long contentMetadataId);

    @Modifying
    @Query("update Poster p set p.filename = :filename, p.contentType = :contentType where p.contentMetadata.id = :metadataId")
    int updatePosterByContentMetadataId(Long metadataId, String filename, String contentType);
}
