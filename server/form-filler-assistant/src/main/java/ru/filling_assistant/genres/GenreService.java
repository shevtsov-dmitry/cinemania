package ru.filling_assistant.genres;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.filling_assistant.common.ContentAssistService;
import ru.filling_assistant.genres.Genre;
import ru.filling_assistant.genres.GenreRepo;

import java.util.List;
import java.util.StringJoiner;

@Service
public class GenreService extends ContentAssistService<Genre> {

    private final GenreRepo repo;
    private final Pageable foundGenreAmountRestriction = PageRequest.of(0, 5);

    public GenreService(GenreRepo repo) {
        super(repo);
        this.repo = repo;
    }

    public List<String> findMatchedGenres(String sequence) {
        return repo.getGenresNamesBySimilarStringSequence(sequence, foundGenreAmountRestriction);
    }

    @Override
    public List<Genre> saveWithoutDuplicates(List<Genre> receivedEntities) {
        return super.saveWithoutDuplicates(receivedEntities);
    }

    @Transactional
    public ResponseEntity<String> deleteGenres(List<String> genreNamesToDelete) {
        List<String> notAddedGenres = genreNamesToDelete.stream().filter(name -> repo.deleteByName(name) == 0).toList();
        StringJoiner notAddedGenresSJ = new StringJoiner(", ", "[", "]");
        notAddedGenres.forEach(notAddedGenresSJ::add);

        return notAddedGenres.isEmpty() ?
                ResponseEntity.ok("All requested entities has been deleted successfully.") :
                ResponseEntity.badRequest().body(STR."\{notAddedGenres.size()} entities were not deleted from the database. \{notAddedGenresSJ.toString()}");
    }

}