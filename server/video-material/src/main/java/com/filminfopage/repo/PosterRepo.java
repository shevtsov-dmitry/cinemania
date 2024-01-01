package com.filminfopage.repo;

import com.filminfopage.model.Poster;
import com.mongodb.client.result.DeleteResult;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PosterRepo extends MongoRepository<Poster, String> {
    Poster getPosterById(String id);
    Poster getPosterByTitle(String title);

    long deletePosterById(String id);

    long deletePosterByTitle(String title);
}
