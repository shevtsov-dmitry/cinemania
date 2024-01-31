package ru.content_assist_with_input.filling_assistant.genres.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.content_assist_with_input.filling_assistant.common.ContentAssistService;
import ru.content_assist_with_input.filling_assistant.genres.model.Genre;
import ru.content_assist_with_input.filling_assistant.genres.repo.GenreRepo;

import java.util.List;
import java.util.StringJoiner;

@Service
public class GenreService extends ContentAssistService<Genre> {

    private final GenreRepo repo;
    private final Pageable foundGenreAmountRestriction = PageRequest.of(0, 5);

    @Autowired
    public GenreService(GenreRepo repo) {
        super(repo);
        this.repo = repo;
    }

    public List<String> findMatchedGenres(String sequence) {
        return repo.getGenresNamesBySimilarStringSequence(sequence, foundGenreAmountRestriction);
    }

    @Override
    public List<String> saveWithoutDuplicates(List<Genre> receivedEntities) {
        return super.saveWithoutDuplicates(receivedEntities);
    }

    @Transactional
    public ResponseEntity<String> deleteGenres(List<String> genreNamesToDelete) {
        List<String> notAddedGenres = genreNamesToDelete.stream().filter(name -> repo.deleteByName(name) == 0).toList();
        StringJoiner notAddedGenresSJ = new StringJoiner(", ", "[", "]");
        notAddedGenres.forEach(notAddedGenresSJ::add);

        return notAddedGenres.isEmpty() ?
                ResponseEntity.ok("All requested genres has been deleted successfully.") :
                ResponseEntity.badRequest().body(STR."\{notAddedGenres.size()} genres were not deleted from the database. \{notAddedGenresSJ.toString()}");
    }

    public List<String> saveNewGenres(List<Genre> genres) throws UnsupportedOperationException, DataIntegrityViolationException {
        return repo.saveAll(genres).stream().map(Genre::getName).toList();
    }
}