package ru.filling_assistant.genre;

import ru.filling_assistant.common.BaseRepo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepo extends BaseRepo<Genre> {

    @Query("SELECT g.name FROM Genre g WHERE g.name LIKE ?1%")
    List<String> getGenresNamesBySimilarStringSequence(String sequence, Pageable pageable);

}
