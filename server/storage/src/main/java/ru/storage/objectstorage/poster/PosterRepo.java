package ru.storage.objectstorage.poster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterRepo extends JpaRepository<Poster, Long> {


}
