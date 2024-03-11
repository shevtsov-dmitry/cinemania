package ru.video_material.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Document
@NoArgsConstructor
public class ContentMetadata {
    private String id;
    private String title;
    private String releaseDate;
    private String country;
    private String genre;
    private Integer age;
    private String posterId;
    private String videoId;
    private float rating;
    private LocalDateTime createdAt;

    public ContentMetadata(String title, String releaseDate,
                           String country, String genre, Integer age,
                           String posterId, String videoId, Float rating) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.country = country;
        this.genre = genre;
        this.age = age;
        this.posterId = posterId;
        this.videoId = videoId;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }

//    @PrePersist
    public void validateDate() {
        Pattern regexp = Pattern.compile("(19|20)\\d{2}-(0[1-9]|1[1,2])-(0[1-9]|[12][0-9]|3[01])");
        Matcher matcher = regexp.matcher(releaseDate);
        if (!matcher.find()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Date format is not supported. Expected yyyy-MM-dd.");
        }
    }
}
