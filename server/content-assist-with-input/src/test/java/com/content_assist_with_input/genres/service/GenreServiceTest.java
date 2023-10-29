package com.content_assist_with_input.genres.service;

import com.content_assist_with_input.genres.model.Genre;
import com.content_assist_with_input.genres.repo.GenreRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
@ComponentScan("com/content_assist_with_input/genres/service")
class GenreServiceTest {

    private final GenreService service;
    private final GenreRepo repo;

    @Autowired
    GenreServiceTest(GenreService service, GenreRepo repo) {
        this.service = service;
        this.repo = repo;
    }

    Logger log = LoggerFactory.getLogger(this.getClass());

    // ! BREAKING TESTS

    @Test
    void addGenresWithDups() {
        List<String> genreNames = List.of("Сказки", "Короткометражные", "Образовательные", "Сказки", "Сказки", "Образовательные");
        List<Genre> genres = new ArrayList<>(genreNames.size());
        genreNames.forEach(name -> genres.add(new Genre(name)));
        String answerMessage = service.saveAgainButWithoutDuplicates(genres);
        log.info("answerMessage: " + answerMessage);
        List<String> expectedResult = List.of("Сказки", "Короткометражные", "Образовательные");
        for (String name : expectedResult) {
            Assertions.assertNotNull(repo.findByName(name));
        }
    }
    @Test
    void addGenresWithDupsThenAgainButWithNewElements() {
        List<String> genres = List.of("Сказки", "Короткометражные", "Образовательные", "Сказки", "Сказки", "Образовательные");
        List<String> genresWithNewEls = List.of("Сказки", "Короткометражные", "Образовательные", "Сказки", "Сказки", "Образовательные", "NEW", "ELEMENTS", "TESTING");

    }


    @Test
    @RepeatedTest(2)
    void insertOneGenreMoreThanOnce() {
        Genre genre = new Genre("комедия");
//        repo.save(genre);
    }

    @Test
    void addEmptyList(){

    }


}