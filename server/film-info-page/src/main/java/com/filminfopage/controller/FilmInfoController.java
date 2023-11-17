package com.filminfopage.controller;

import com.filminfopage.model.FilmInfo;
import com.filminfopage.repo.FilmInfoRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/film-info")
public class FilmInfoController {

    private final FilmInfoRepo repo;

    @Autowired
    public FilmInfoController(FilmInfoRepo repo) {
        this.repo = repo;
    }

    @PostMapping("/add")
    public boolean saveData(@RequestBody FilmInfo filmInfo) {
        FilmInfo saved = repo.save(filmInfo);
        return saved == null;
    }

    @GetMapping("/get/latest-saved")
    public FilmInfo getLatestSaved() {
        List<FilmInfo> singleton = repo.getLastSaved();
        if (singleton == null) {
            return new FilmInfo();
        }
        return singleton.get(0);
    }

    @GetMapping("/get/all")
    public List<FilmInfo> getAllFilmsInfo() {
        return repo.findAll();
    }
}
