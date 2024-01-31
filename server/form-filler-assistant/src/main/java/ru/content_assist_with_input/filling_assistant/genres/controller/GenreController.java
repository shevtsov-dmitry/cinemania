package ru.content_assist_with_input.filling_assistant.genres.controller;

import com.google.gson.Gson;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import ru.content_assist_with_input.filling_assistant.genres.model.Genre;
import ru.content_assist_with_input.filling_assistant.genres.repo.GenreRepo;
import ru.content_assist_with_input.filling_assistant.genres.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fillingAssistants/genres")
public class GenreController {

    private final GenreService service;
    private final GenreRepo repo;
    private final Gson gson = new Gson();

    @Autowired
    public GenreController(GenreService service, GenreRepo repo) {
        this.service = service;
        this.repo = repo;
    }

    @PostMapping("/add/one")
    public ResponseEntity<String> addNewGenre(@RequestParam String name) throws DataIntegrityViolationException {
        Genre genre = new Genre(name);
        if (genre.getName().isBlank()) {
            return ResponseEntity.badRequest().body("incoming parameter data is empty.");
        }
        try {
            Genre savedGenre = repo.save(genre);
            return ResponseEntity.ok(savedGenre.getId().toString());
        } catch (Exception e) {
            final List<Genre> extensibleMonoList = new ArrayList<>(List.of(genre));
            String json = gson.toJson(service.saveWithoutDuplicates(extensibleMonoList));
            return ResponseEntity.badRequest().body(json);
        }
    }

    @PostMapping("/add/many")
    public ResponseEntity<String> saveNewGenres(@RequestBody List<String> genreNames) {
        genreNames.removeIf(String::isBlank);
        List<Genre> genres = genreNames.stream().map(Genre::new).toList();
        try {
            return ResponseEntity.ok(gson.toJson(service.saveNewGenres(genres)));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.ok(gson.toJson(service.saveWithoutDuplicates(genres)));
        }
    }

    @GetMapping("/get/bySequence")
    public ResponseEntity<List<String>> findGenres(@RequestParam String sequence) {
        List<String> matchedGenres = service.findMatchedGenres(sequence);
        return ResponseEntity.ok(matchedGenres);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRequestedGenres(@RequestBody List<String> genreNames) {
        return service.deleteGenres(genreNames);
    }

}
