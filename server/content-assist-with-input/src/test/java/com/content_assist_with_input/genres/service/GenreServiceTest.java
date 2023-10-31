package com.content_assist_with_input.genres.service;

import com.content_assist_with_input.genres.model.Genre;
import com.content_assist_with_input.genres.repo.GenreRepo;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    // testing of saveWithoutDuplicates()
    @Test
    void addGenresWithDups() {
        List<String> genreNames = List.of("Сказки", "Короткометражные", "Образовательные", "Сказки", "Сказки", "Образовательные");
        List<Genre> genres = new ArrayList<>(genreNames.size());
        genreNames.forEach(name -> genres.add(new Genre(name)));
        String answerMessage = service.saveWithoutDuplicates(genres);
        List<String> expectedResult = List.of("Сказки", "Короткометражные", "Образовательные");
        for (String name : expectedResult) {
            assertNotNull(repo.findByName(name));
        }
    }

    @Test
    void addGenresWithDupsThenAgainButWithNewElements() {
        List<String> genreNames = List.of("Сказки", "Короткометражные", "Образовательные", "Сказки", "Сказки", "Образовательные");
        List<Genre> newGenres = List.of(new Genre("NEW"), new Genre("ELEMENTS"), new Genre("TESTING"));
        // save initial genres
        List<Genre> genres = genreNames.stream().map(Genre::new).toList();
        service.saveWithoutDuplicates(genres);
        // fill list with new genre names
        List<String> genreNamesWithNewEls = new ArrayList<>();
        genreNamesWithNewEls.addAll(genreNames);
        genreNamesWithNewEls.addAll(newGenres.stream().map(Genre::getName).toList());
        List<Genre> genresWithNewEls = new ArrayList<>(genreNamesWithNewEls.stream().map(Genre::new).toList());
        // save second time the same elements with added new elements
        service.saveWithoutDuplicates(genresWithNewEls);
        // expecting that new elements will be in database and old ones will not duplicate (violate data integration)
        for (Genre genre : newGenres) {
            assertNotNull(repo.findByName(genre.getName()));
        }
    }

    @RepeatedTest(2)
    void insertOneGenreMoreThanOnce() {
        Genre genre = new Genre("комедия");
        service.saveWithoutDuplicates(new ArrayList<>(List.of(genre)));
        String answer = service.saveWithoutDuplicates(new ArrayList<>(List.of(genre)));
        assertEquals(answer, "Cannot save because already exist in database.");
        assertNotNull(repo.findByName(genre.getName()));
    }

    // testing of findMatchedGenres(String sequence)
    @Test
    void FindGenreNamesByPreparedSequences() {
        List<Genre> genres = List.of(new Genre("Драма"), new Genre("Драматургия"), new Genre("Другое"), new Genre("Дружба"));
        repo.saveAll(genres);

        List<String> case1Actual = service.findMatchedGenres("Д");
        List<String> case1Expected = List.of("Драма", "Драматургия", "Другое", "Дружба");

        List<String> case2Actual = service.findMatchedGenres("Др");
        List<String> case2Expected = List.of("Драма", "Драматургия", "Другое", "Дружба");

        List<String> case3Actual = service.findMatchedGenres("Дру");
        List<String> case3Expected = List.of("Другое", "Дружба");

        List<String> case4Actual = service.findMatchedGenres("Друж");
        List<String> case4Expected = List.of("Дружба");

        List<String> case5Actual = service.findMatchedGenres("Дра");
        List<String> case5Expected = List.of("Драма", "Драматургия");

        assertLinesMatch(case1Expected, case1Actual);
        assertLinesMatch(case2Expected, case2Actual);
        assertLinesMatch(case3Expected, case3Actual);
        assertLinesMatch(case4Expected, case4Actual);
        assertLinesMatch(case5Expected, case5Actual);
    }

    @Test
    void FindNotMoreThanFiveGenreNamesBySequence() {
        List<Genre> genres = List.of(
                new Genre("Драма"), new Genre("Драматургия"),
                new Genre("Другое"), new Genre("Дружба"),
                new Genre("Дорама"), new Genre("Диджитал"),
                new Genre("Догма"), new Genre("Дэнс")
        );
        repo.saveAll(genres);

        List<String> genresExpected = List.of("Драма", "Драматургия", "Другое", "Дружба", "Дорама");
        List<String> genresActual = service.findMatchedGenres("Д");



        assertLinesMatch(genresExpected, genresActual);
    }

}