package ru.storage.metadata;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
public class ContentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String title;
    @NonNull
    private String releaseDate;
    @NonNull
    private String country;
    @NonNull
    private String mainGenre;
    @NonNull
    private List<String> subGenres;
    @NonNull
    private Integer age;
    @NonNull
    private Double rating;
    private String posterId;
    private String videoId;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // @PrePersist
    // public void validateDate() {
    // Pattern regexp =
    // Pattern.compile("(19|20)\\d{2}-(0[1-9]|1[1,2])-(0[1-9]|[12][0-9]|3[01])");
    // Matcher matcher = regexp.matcher(releaseDate);
    // if (!matcher.find()) {
    // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
    // "Date format is not supported. Expected yyyy-MM-dd.");
    // }
    // }

}
