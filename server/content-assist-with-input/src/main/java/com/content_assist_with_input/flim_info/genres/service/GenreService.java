package com.content_assist_with_input.flim_info.genres.service;

import com.content_assist_with_input.flim_info.genres.model.Genre;
import com.content_assist_with_input.flim_info.genres.repo.GenreRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenreService {

    private final GenreRepo repo;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Pageable foundGenreAmountRestriction = PageRequest.of(0,5);

    @Autowired
    public GenreService(GenreRepo repo) {
        this.repo = repo;
    }

    public List<String> findMatchedGenres(String sequence) {
        List<String> matches = repo.getGenresNamesBySimilarStringSequence(sequence, foundGenreAmountRestriction);
        log.info("genres: {}", matches);
        return matches;
    }

    public String saveWithoutDuplicates(List<Genre> receivedGenres) {
        List<Genre> genres = repo.findAll();
        if (!genres.isEmpty()) { // in case if at least one genre already exists in database
            List<String> allGenresNames = genres.stream().map(Genre::getName).toList();
            receivedGenres.removeIf(genre -> allGenresNames.contains(genre.getName()));
            if (receivedGenres.isEmpty()) {
                return "Cannot save because already exist in database.";
            } else {
                repo.saveAll(receivedGenres);
                List<String> receivedGenresName = receivedGenres.stream().map(Genre::getName).toList();
                // parse string answer
                return parseStringAnswer(receivedGenresName);
            }
        } else { // else we add new genres from received list by removing duplicates.
            Map<String, Genre> stringContentGenreObject = new HashMap<>();
            for (Genre genre : receivedGenres) {
                stringContentGenreObject.put(genre.getName(), genre);
            }
            List<String> genresWithoutDuplicates = new ArrayList<>(stringContentGenreObject.size());
            for (Map.Entry<String, Genre> stringGenreEntry : stringContentGenreObject.entrySet()) {
                String key = stringGenreEntry.getKey();
                genresWithoutDuplicates.add(key);
                repo.save(stringContentGenreObject.get(key));
            }
            // parse string answer
            return parseStringAnswer(genresWithoutDuplicates);
        }
    }

    private static String parseStringAnswer(List<String> receivedGenresName) {
        StringBuilder builder = new StringBuilder();
        builder.append("Successfully added new genres: ");
        for (String genre : receivedGenresName) {
            builder.append(genre).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);
        builder.append(".");
        return builder.toString();
    }

}