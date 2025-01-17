package ru.storage.content;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.storage.content.poster.PosterMetadata;
import ru.storage.content.video.VideoMetadata;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class ContentDetails {

    @Id
    private String id;
    @NotNull(message = "Необходимо указать название")
    private String title;
    @NotNull(message = "Необходимо указать дату выпуска")
    private LocalDate releaseDate;
    @NotNull(message = "Необходимо указать страну")
    private String country;
    @NotNull(message = "Необходимо указать главный жанр")
    private String mainGenre;
    private List<String> subGenres;
    @Min(0)
    @Max(21)
    @NotNull(message = "Необходимо указать возрастное ограничение")
    private Integer age;
    private Double rating;
    @DBRef
    private PosterMetadata posterMetadata;
    @DBRef
    private VideoMetadata videoMetadata;
    @JsonIgnore
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

}