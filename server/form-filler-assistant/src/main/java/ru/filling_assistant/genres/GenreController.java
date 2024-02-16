package ru.filling_assistant.genres;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.filling_assistant.common.ContentAssistController;
import ru.filling_assistant.country.Country;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fillingAssistants/genres")
public class GenreController extends ContentAssistController<Genre> {

    private final GenreService service;
    @Autowired
    public GenreController(GenreService service) {
        super(service);
        this.service = service;
    }

    @PostMapping("/add/one")
    public ResponseEntity<String> addNewGenre(@RequestParam String genreName) {
        return super.tryToSaveOneEntity(new Genre(genreName));
    }

    @PostMapping("/add/many")
    public ResponseEntity<List<String>> saveNewGenres(@RequestBody List<String> genreNames) {
        List<Genre> genres = new ArrayList<>(genreNames.size());
        genreNames.forEach(name -> genres.add(new Genre(name)));
        return super.tryToSaveListOfEntities(genres);
    }

    @GetMapping("/get/bySequence")
    public ResponseEntity<List<String>> findGenres(@RequestParam String sequence) {
        return ResponseEntity.ok(service.findMatchedGenres(sequence));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRequestedGenres(@RequestBody List<String> genreNames) {
        return service.deleteGenres(genreNames);
    }

}
