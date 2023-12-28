package com.filminfopage.repo;

import com.filminfopage.model.Poster;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PosterRepo extends MongoRepository<Poster, String> {
    Poster getPosterById(String id);
    Poster getPosterByTitle(String title);
}
