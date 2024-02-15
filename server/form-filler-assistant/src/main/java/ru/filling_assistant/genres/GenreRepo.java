package ru.filling_assistant.genres;

import ru.filling_assistant.genres.Genre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepo extends JpaRepository<Genre, Long> {
    Genre findByName(String name);

    @Query("FROM Genre g WHERE g.name LIKE ?1%")
    List<String> getGenresNamesBySimilarStringSequence(String sequence, Pageable pageable);

    int deleteByName(String name);
}
