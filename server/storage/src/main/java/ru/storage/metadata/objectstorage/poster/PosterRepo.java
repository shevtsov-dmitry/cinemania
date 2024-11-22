package ru.storage.metadata.objectstorage.poster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.storage.metadata.Content;

@Repository
public interface PosterRepo extends JpaRepository<Poster, Long> {

    @Modifying
    @Query("delete from Poster e where e.contentMetadata.id = :contentMetadataId")
    void deleteByContentMetadataId(Long contentMetadataId);

    @Modifying
    @Query("update Poster p set p.filename = :filename, p.contentType = :contentType where p.contentMetadata = :metadata")
    int updatePosterByContentMetadata(Content metadata, String filename, String contentType);
}
