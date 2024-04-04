package ru.video_material.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Document
public class ContentMetadata {
    private String id;
    private String title;
    private String releaseDate;
    private String country;
    private String mainGenre;
    private List<String> subGenres;
    private Integer age;
    private String posterId;
    private String videoId;
    private float rating;
    private LocalDateTime createdAt;

    public ContentMetadata(String title, String releaseDate,
            String country, String mainGenre, List<String> subGenres, Integer age,
            String posterId, String videoId, Float rating) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.country = country;
        this.mainGenre = mainGenre;
        this.subGenres = subGenres;
        this.age = age;
        this.posterId = posterId;
        this.videoId = videoId;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }

    public ContentMetadata() {
    }

    // @PrePersist
    public void validateDate() {
        Pattern regexp = Pattern.compile("(19|20)\\d{2}-(0[1-9]|1[1,2])-(0[1-9]|[12][0-9]|3[01])");
        Matcher matcher = regexp.matcher(releaseDate);
        if (!matcher.find()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Date format is not supported. Expected yyyy-MM-dd.");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMainGenre() {
        return mainGenre;
    }

    public void setMainGenre(String mainGenre) {
        this.mainGenre = mainGenre;
    }

    public List<String> subGenres() {
        return subGenres;
    }

    public void setSubGenres(List<String> subGenres) {
        this.subGenres = subGenres;
    }

    public List<String> getSubGenres() {
        return subGenres;
    }

}
