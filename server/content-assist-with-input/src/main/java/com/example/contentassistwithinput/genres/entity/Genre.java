package com.example.contentassistwithinput.genres.entity;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @NonNull
    private String genre;

    public Genre(@NonNull String genre) {
        this.genre = genre;
    }

    public Genre() {
    }

    @NonNull
    public String getGenre() {
        return genre;
    }

    public void setGenre(@NonNull String genre) {
        this.genre = genre;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
