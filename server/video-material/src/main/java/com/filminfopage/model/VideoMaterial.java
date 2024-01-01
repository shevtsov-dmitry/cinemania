package com.filminfopage.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class VideoMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Long id;
    @NonNull
    private String filmName;
    @NonNull
    private Date releaseDate;
    @Column(length = 50)
    @NonNull
    private String country;
    @Column(length = 255)
    @NonNull
    private String genre;
    @NonNull
    private int age;
    @NonNull
    private String imageUrl;
    private String watchTime;
    private float rating;


    public VideoMaterial(Long id, @NonNull String name, @NonNull Date releaseDate, @NonNull String country,
                         @NonNull String genre, int age, @NonNull String imageUrl, String watchTime, float rating) {
        this.id = id;
        this.filmName = name;
        this.releaseDate = releaseDate;
        this.country = country;
        this.genre = genre;
        this.age = age;
        this.imageUrl = imageUrl;
        this.watchTime = watchTime;
        this.rating = rating;
    }

    public VideoMaterial(@NonNull String filmName, @NonNull Date releaseDate, @NonNull String country,
                         @NonNull String genre, int age, @NonNull String imageUrl, String watchTime, float rating) {
        this.filmName = filmName;
        this.releaseDate = releaseDate;
        this.country = country;
        this.genre = genre;
        this.age = age;
        this.imageUrl = imageUrl;
        this.watchTime = watchTime;
        this.rating = rating;
    }

    public VideoMaterial(@NonNull String filmName, @NonNull Date releaseDate, @NonNull String country,
                         @NonNull String genre, int age, @NonNull String imageUrl) {
        this.filmName = filmName;
        this.releaseDate = releaseDate;
        this.country = country;
        this.genre = genre;
        this.age = age;
        this.imageUrl = imageUrl;
    }

}
