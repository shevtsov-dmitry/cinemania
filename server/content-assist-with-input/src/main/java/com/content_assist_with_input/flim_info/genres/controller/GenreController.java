package ru.content_assist_with_input.filling_assistant.genres.controller;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import ru.content_assist_with_input.filling_assistant.genres.model.Genre;
import ru.content_assist_with_input.filling_assistant.genres.repo.GenreRepo;
import ru.content_assist_with_input.filling_assistant.genres.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fillingAssistants/genres")
public class GenreController {

    private final GenreService service;
    private final GenreRepo repo;

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
            String message = service.saveWithoutDuplicates(extensibleMonoList);
            return ResponseEntity.ok(message);
        }
    }

    @PostMapping("/add/many")
    public String addNewGenres(@RequestBody Map<String, List<Genre>> jsonMap) throws DataIntegrityViolationException {
        List<Genre> genres = jsonMap.get("genres");
        try {
            genres.removeIf(genre -> genre.getName().equals("") || genre.getName().equals(" "));
            repo.saveAll(genres);
            return "new genres have been added successfully.";
        } catch (Exception e) {
            return service.saveWithoutDuplicates(genres);
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
