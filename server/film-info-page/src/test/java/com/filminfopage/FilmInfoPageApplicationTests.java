package com.filminfopage;

import com.filminfopage.entity.FilmInfo;
import com.filminfopage.repo.FilmInfoRepo;
import org.junit.gen5.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Calendar;
import java.util.Date;

//@SpringBootTest
@DataJpaTest
@Rollback(value = false)
@AutoConfigureTestDatabase
class FilmInfoPageApplicationTests {


    @Autowired
    private FilmInfoRepo filmInfoRepo;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    void insertFullFilmInfoOnce() {
        FilmInfo film = new FilmInfo(
                "film",
                new Date(2012, Calendar.JUNE, 20),
                "USA",
                "horror",
                18,
                "url/myImage/url",
                "1:30:05",
                5.4F
        );

        FilmInfo savedFilm = filmInfoRepo.save(film);
        Assertions.assertNotEquals(savedFilm.getId(), 1, "did not create film info");

    }



}
`