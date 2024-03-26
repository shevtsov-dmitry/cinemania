package ru.filling_assistant.genre;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.filling_assistant.common.BaseService;

import java.util.List;

@Service
public class GenreService extends BaseService<Genre> {

    private final GenreRepo repo;

    @Autowired
    public GenreService(GenreRepo repo) {
        super(repo);
        this.repo = repo;
    }

    @Override
    public List<Genre> saveWithoutDuplicates(List<Genre> receivedEntities) {
        return super.saveWithoutDuplicates(receivedEntities);
    }

    public List<Genre> getAllGenres() {
        return super.getAllEntities();
    }

    @Transactional
    public ResponseEntity<String> deleteGenres(List<String> genreNamesToDelete) {
        return super.deleteEntitiesByName(genreNamesToDelete);
    }

}