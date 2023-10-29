package com.example.contentassistwithinput.genres.service;

import com.example.contentassistwithinput.genres.model.Genre;
import com.example.contentassistwithinput.genres.repo.GenreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenreService {

    private final GenreRepo repo;

    @Autowired
    public GenreService(GenreRepo repo) {
        this.repo = repo;
    }

    public List<String> findMatchedGenres(String stringSequence) {
        List<String> matches = new ArrayList<>();
        // TODO
        return matches;
    }

    public String saveAgainButWithoutDuplicates(String genreFromQuery) {
        // TODO
        return null;
    }

    public String saveAgainButWithoutDuplicates(List<Genre> receivedGenres) {
        List<Genre> allGenres = repo.findAll();
        if (!allGenres.isEmpty()) { // in case if at least one genre already exists in database
            receivedGenres.removeIf(allGenres::contains);
            if (receivedGenres.isEmpty()) {
                return "Cannot save genres because all of them already exist in database.";
            } else {
                // parse string answer
                StringBuilder builder = new StringBuilder();
                builder.append("Successfully added new genres: ");
                for (Genre genre : receivedGenres) {
                    repo.save(genre);
                    builder.append(genre.toString()).append(", ");
                }
                builder.delete(builder.length() - 2, builder.length() - 1);
                builder.append(".");
                return builder.toString();
            }
        } else { // else we add new genres from received list by removing duplicates.
            Map<String, Genre> stringContentGenreObject = new HashMap<>();
            for (Genre genre : receivedGenres) {
                stringContentGenreObject.put(genre.getGenre(), genre);
            }
            List<String> genresWithoutDuplicates = new ArrayList<>(stringContentGenreObject.size());
            for (Map.Entry<String, Genre> stringGenreEntry : stringContentGenreObject.entrySet()) {
                String key = stringGenreEntry.getKey();
                genresWithoutDuplicates.add(key);
                repo.save(stringContentGenreObject.get(key));
            }
            // parse string answer
            StringBuilder builder = new StringBuilder();
            builder.append("Successfully added new genres: ");
            for (String genre : genresWithoutDuplicates) {
                builder.append(genre).append(", ");
            }
            builder.delete(builder.length() - 2, builder.length() - 1);
            builder.append(".");
            return builder.toString();
        }
    }

//    public void save(Genre genre) {
//        repo.save(genre);
//    }
//
//    public void saveAll(List<Genre> genres) {
//        repo.saveAll(genres);
//    }
}
