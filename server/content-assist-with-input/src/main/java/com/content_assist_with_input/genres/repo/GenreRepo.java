package com.content_assist_with_input.genres.repo;

import com.content_assist_with_input.genres.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepo extends JpaRepository<Genre, Long> {
}
