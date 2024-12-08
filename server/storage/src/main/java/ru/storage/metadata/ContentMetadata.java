package ru.storage.metadata;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.video.Video;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Document
public class ContentMetadata {

    private Long id;
    @NotNull(message = "Необходимо указать название")
    private String title;
    @NotNull(message = "Необходимо указать дату выпуска")
    private Date releaseDate;
    @NotNull(message = "Необходимо указать страну")
    private String country;
    @NotNull(message = "Необходимо указать главный жанр")
    private String mainGenre;
    private List<SubGenre> subGenres;
    @Min(0)
    @Max(21)
    @NotNull(message = "Необходимо указать возрастное ограничение")
    private Integer age;
    private Double rating;
    @DBRef
    private Poster poster;
    @DBRef
    private Video video;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    public record SubGenre(Long id, String name) {
    }

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
