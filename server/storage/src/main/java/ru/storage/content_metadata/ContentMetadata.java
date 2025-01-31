package ru.storage.content_metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.content_metadata.country.Country;
import ru.storage.content_metadata.genre.Genre;
import ru.storage.content_metadata.poster.Poster;
import ru.storage.content_metadata.trailer.Trailer;
import ru.storage.content_metadata.tv_series.TvSeries;
import ru.storage.content_metadata.video.Video;
import ru.storage.person.filming_group.FilmingGroup;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class ContentMetadata {

  @Id private String id;

  @NotNull(message = "Необходимо указать название")
  private String title;

  @NotNull(message = "Необходимо указать дату выпуска")
  private LocalDate releaseDate;

  @DBRef
  @NotNull(message = "Необходимо указать страну")
  private Country country;

  @DBRef
  @NotNull(message = "Необходимо указать главный жанр")
  private Genre mainGenre;

  @DBRef private List<Genre> subGenres;
  private String description;

  // TODO make this field but without recursive stack overflow
  //    List<ContentMetadata> relatedShows;
  @Min(0)
  @Max(21)
  @NotNull(message = "Необходимо указать возрастное ограничение")
  private Integer age;

  private Double rating;
  @DBRef private Poster poster;
  @DBRef private Video singleVideoShow;
  @DBRef private FilmingGroup filmingGroup;
  @DBRef private TvSeries tvSeries;
  @DBRef private Trailer trailer;
  @JsonIgnore @CreatedDate private LocalDateTime createdAt = LocalDateTime.now();
}
