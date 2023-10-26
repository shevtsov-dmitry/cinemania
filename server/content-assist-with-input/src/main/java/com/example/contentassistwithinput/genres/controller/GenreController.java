package com.example.contentassistwithinput.genres.controller;

import com.example.contentassistwithinput.genres.model.Genre;
import com.example.contentassistwithinput.genres.repo.GenreRepo;
import com.example.contentassistwithinput.genres.service.GenreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/film-info-genre")
public class GenreController {

    Logger logger = LoggerFactory.getLogger(GenreController.class);
    private GenreRepo repo;
    private final GenreService service = new GenreService(repo);

    public GenreController(GenreRepo repo) {
        this.repo = repo;
    }

    @PostMapping("/add-new-genre")
    public String addNewGenre(@RequestParam Genre genre) {
        try {
            repo.save(genre);
            return "add new genre successfully";
        } catch (Exception e) {
            logger.info(String.valueOf(e));
            return "couldn't add new genre";
        }
    }

    @PostMapping("/add-new-genres")
    public String addNewGenres(@RequestBody Map<String, List<Genre>> jsonMap){
        try {
            List<Genre> genres = jsonMap.get("genres");
            repo.saveAll(genres);
            return "new genres have been added successfully.";
        } catch (Exception e) {
            logger.info(String.valueOf(e));
            return "couldn't add new genres to database.";
        }
    }

    @GetMapping("/get-genre")
    public List<String> findGenre(@RequestParam String stringSequence){
        return service.findMatchedGenres(stringSequence);
    }


}
