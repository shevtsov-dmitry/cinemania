package com.example.contentassistwithinput.genres.repo;

import com.example.contentassistwithinput.genres.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepo extends JpaRepository<Genre, Long> {
}
