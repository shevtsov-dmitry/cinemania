package com.filminfopage.entity;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;

@Entity
public class FilmInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Long id;
    @NonNull
    private String name;
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
    private String watchTime; // TODO make time authentication for uploaded file automatic
    private float rating;


    public FilmInfo() {

    }

    public FilmInfo(Long id, @NonNull String name, @NonNull Date releaseDate, @NonNull String country,
                    @NonNull String genre, int age, @NonNull String imageUrl, String watchTime, float rating) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.country = country;
        this.genre = genre;
        this.age = age;
        this.imageUrl = imageUrl;
        this.watchTime = watchTime;
        this.rating = rating;
    }

    public FilmInfo(@NonNull String name, @NonNull Date releaseDate, @NonNull String country,
                    @NonNull String genre, int age, @NonNull String imageUrl, String watchTime, float rating) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.country = country;
        this.genre = genre;
        this.age = age;
        this.imageUrl = imageUrl;
        this.watchTime = watchTime;
        this.rating = rating;
    }

    public FilmInfo(@NonNull String name, @NonNull Date releaseDate, @NonNull String country,
                    @NonNull String genre, int age, @NonNull String imageUrl) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.country = country;
        this.genre = genre;
        this.age = age;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(String watchTime) {
        this.watchTime = watchTime;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}