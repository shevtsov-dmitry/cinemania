package com.filminfopage.controller;

import com.filminfopage.entity.FilmInfo;
import com.filminfopage.repo.FilmInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/add-new-film-info")
public class FilmInfoController {


    private final FilmInfoRepo filmInfoRepo;

    public FilmInfoController(FilmInfoRepo filmInfoRepo) {
        this.filmInfoRepo = filmInfoRepo;
    }

    @GetMapping
    public boolean saveData(@RequestBody FilmInfo filmInfo){

        FilmInfo saved = filmInfoRepo.save(filmInfo);

        return true;
    }
}