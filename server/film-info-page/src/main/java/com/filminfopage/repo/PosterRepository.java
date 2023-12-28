package com.filminfopage.repo;

import com.filminfopage.model.Poster;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PosterRepository extends MongoRepository<Poster, String> {
}
