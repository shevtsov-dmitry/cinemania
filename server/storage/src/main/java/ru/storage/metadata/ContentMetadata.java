package ru.storage.metadata;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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

    @Id
    private String id;
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

    @Data
    public class SubGenre {
        private Long id;
        private String name;
    }
}