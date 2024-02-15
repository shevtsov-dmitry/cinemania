package ru.filling_assistant.genres.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.filling_assistant.common.ContentAssistController;
import ru.filling_assistant.genres.model.Genre;
import ru.filling_assistant.genres.service.GenreService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fillingAssistants/genres")
public class GenreController {

    private final GenreService service;
    private final ContentAssistController<Genre> commonController;
    @Autowired
    public GenreController(Class<Genre> genreClass, GenreService service) {
        this.service = service;
        commonController = new ContentAssistController<>(genreClass, service);
    }

    @PostMapping("/add/one")
    public ResponseEntity<String> addNewGenre(@RequestParam String name) {
        return commonController.addOne(name);
    }

    @PostMapping("/add/many")
    public ResponseEntity<String> saveNewGenres(@RequestBody List<String> genreNames) {
        return commonController.addMany(genreNames);
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
