package ru.video_material.video.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@Entity
public class VideoMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Long id;
    @NonNull
    private String title;
    @NonNull
    private String releaseDate;
    @Column(length = 50)
    @NonNull
    private String country;
    @Column(length = 255)
    @NonNull
    private String genre;
    @NonNull
    private Integer age;
    @NonNull
    private String posterId;
    @NonNull
    private String videoId;
    private float rating;

    public VideoMetadata(@NonNull String title, @NonNull String releaseDate, @NonNull String country,
                         @NonNull String genre, @NonNull Integer age) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.country = country;
        this.genre = genre;
        this.age = age;
    }

    public VideoMetadata(@NonNull String title, @NonNull String releaseDate,
                         @NonNull String country, @NonNull String genre, @NonNull Integer age,
                         @NonNull String posterId, @NonNull String videoId) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.country = country;
        this.genre = genre;
        this.age = age;
        this.posterId = posterId;
        this.videoId = videoId;
    }

    public VideoMetadata(@NonNull String title, @NonNull String releaseDate,
                         @NonNull String country, @NonNull String genre, @NonNull Integer age,
                         @NonNull String posterId, @NonNull String videoId, Float rating) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.country = country;
        this.genre = genre;
        this.age = age;
        this.posterId = posterId;
        this.videoId = videoId;
        this.rating = rating;
    }

    @PrePersist
    public void validateDate() {
        Pattern regexp = Pattern.compile("(19|20)\\d{2}-(0[1-9]|1[1,2])-(0[1-9]|[12][0-9]|3[01])");
        Matcher matcher = regexp.matcher(releaseDate);
        if (!matcher.find()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Date format is not supported. Expected yyyy-MM-dd.");
        }
    }
}
