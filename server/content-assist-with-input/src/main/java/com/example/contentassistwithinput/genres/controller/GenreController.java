package com.example.contentassistwithinput.genres.controller;

import com.example.contentassistwithinput.genres.entity.Genre;
import com.example.contentassistwithinput.genres.repo.GenreRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/film-info-genre")
public class GenreController {

    Logger logger = LoggerFactory.getLogger(GenreController.class);
    private final GenreRepo repo;

    public GenreController(GenreRepo repo) {
        this.repo = repo;
    }

    @PostMapping("/add-new")
    public String addNewGenre(@RequestBody Genre genre) {
        try {
            repo.save(genre);
            return "add new genre successfully";
        } catch (Exception e) {
            logger.info(String.valueOf(e));
            return "couldn't add new genre";
        }
    }

}
