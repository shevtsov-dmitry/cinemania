package com.example.contentassistwithinput.genres.service;

import com.example.contentassistwithinput.genres.model.Genre;
import com.example.contentassistwithinput.genres.repo.GenreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public String saveAgainButWithoutDuplicates(List<Genre> receivedGenres) {
        List<String> allGenresNames = repo.findAll().stream().map(Genre::getGenre).toList();
        if (!allGenresNames.isEmpty()) { // in case if at least one genre already exists in database
            if(receivedGenres.size() > 2)
                receivedGenres.removeIf(genre -> allGenresNames.contains(genre.getGenre()));
            else if(receivedGenres.size() == 1){
                return "'%s' genre already exists in the database.".formatted(receivedGenres.get(0).getGenre());
            }
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
                builder.delete(builder.length() - 3, builder.length() - 1);
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
            builder.delete(builder.length() - 3, builder.length() - 1);
            builder.append(".");
            return builder.toString();
        }
    }

}
