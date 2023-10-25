package com.filminfopage;

import com.filminfopage.entity.FilmInfo;
import org.junit.gen5.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;

@SpringBootTest
class FilmInfoPageApplicationTests {
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

        Assertions.assertNotEquals(film.getId(), 1, "did not create film info");
    }

}
