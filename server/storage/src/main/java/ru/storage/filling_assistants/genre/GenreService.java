package ru.storage.filling_assistants.genre;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

@Service
public class GenreService {

    private static final Logger log = LoggerFactory.getLogger(GenreService.class);
    private final GenreRepo genreRepo;

    public GenreService(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }

    void save(Genre genre) {
        try {
            genreRepo.save(genre);
        } catch (Exception e) {
            String errmes = "Ошибка при сохранении нового жанра в бд.";
            log.warn("{} {}", errmes, e.getMessage());
            throw new MongoException(errmes);
        }
    }

    void save(List<Genre> genres) {
        try {
            genreRepo.saveAll(genres);
        } catch (Exception e) {
            String errmes = "Ошибка при сохранении новых жанров в бд.";
            log.warn("{} {}", errmes, e.getMessage());
            throw new MongoException(errmes);
        }
    }

    List<String> getAll() {
        return genreRepo.findAll().stream()
                .map(Genre::getName)
                .toList();
    }

    void deleteByName(String name) {
        genreRepo.deleteByName(name);
    }
}
