package com.example.contentassistwithinput.genres.service;

import com.example.contentassistwithinput.genres.model.Genre;
import com.example.contentassistwithinput.genres.repo.GenreRepo;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
class GenreServiceTest {

    @Autowired
    GenreRepo repo;

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    void addGenres() {
        String genreListString = "Короткометражные, Образовательные, Сказки, Сёнэн, Русские, Меха, Театр, Украинские, Эротика, Приключения, Комедии, Мелодрамы, Стендап, Сёнэн-ай, Криминал, Боевые искусства, Военные, Сёдзё-ай, Детективы, Биографические, Романтические, Документальные, Боевики, Детские, Повседневность, Познавательные, Вестерны, Путешествия, Самурайский боевик, Арт-хаус, Семейные, Ужасы, Фэнтези, Пародия, Школа, Мюзиклы, Мультсериалы, Аниме, Советские, Этти, Мистические, Драмы, Триллеры, Полнометражные, Фантастика, Махо-сёдзё, Для взрослых, Сёдзё, Зарубежные, Музыкальные, Кодомо, Концерт, Исторические, Телепередачи, Спортивные, Реальное ТВ";
        String[] genresNames = genreListString.split(", ");
        // add all genres
        for (String genresName : genresNames) {
            Genre genre = new Genre();
            genre.setGenre(genresName);
            repo.save(genre);
        }
        // check matches

        List<Genre> genres = repo.findAll();
        List<String> genreNamesFromDatabase = new ArrayList<>();
        for (Genre genre : genres) {
            genreNamesFromDatabase.add(genre.getGenre());
        }

        for (String genreName : genresNames) {
            assertTrue(genreNamesFromDatabase.contains(genreName));
        }
    }
}