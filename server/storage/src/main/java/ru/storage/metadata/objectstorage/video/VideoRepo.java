package ru.storage.metadata.objectstorage.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepo extends JpaRepository<Video, Long> {

    Optional<Video> findByContentMetadataId(Long contentId);
}
