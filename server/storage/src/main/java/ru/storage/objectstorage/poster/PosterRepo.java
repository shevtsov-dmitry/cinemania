package ru.storage.objectstorage.poster;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.storage.metadata.ContentMetadata;

@Repository
public interface PosterRepo extends JpaRepository<Poster, Long> {
}
