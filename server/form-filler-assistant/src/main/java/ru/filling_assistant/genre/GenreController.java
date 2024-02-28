package ru.filling_assistant.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.filling_assistant.common.BaseController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/fillingAssistants/genres")
public class GenreController extends BaseController<Genre> {

    private final GenreService service;

    @Autowired
    public GenreController(GenreService service) {
        super(service);
        this.service = service;
    }

    @PostMapping("/add/one")
    public ResponseEntity<String> addNewGenre(@RequestParam String name) {
        return super.tryToSaveOneEntity(new Genre(name));
    }

    @PostMapping("/add/many")
    public ResponseEntity<List<Long>> saveNewGenres(@RequestBody List<String> genreNames) {
        List<Genre> genres = new ArrayList<>(genreNames.size());
        genreNames.forEach(name -> genres.add(new Genre(name)));
        return super.tryToSaveListOfEntities(genres);
    }

    @GetMapping(value = "/get/bySequence", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<String>> findGenres(@RequestParam String sequence) {
        if(sequence.isBlank()) return ResponseEntity.ok(Collections.emptyList());
        return ResponseEntity.ok(service.findMatchedGenres(sequence));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRequestedGenres(@RequestBody List<String> genreNames) {
        return service.deleteGenres(genreNames);
    }

}
