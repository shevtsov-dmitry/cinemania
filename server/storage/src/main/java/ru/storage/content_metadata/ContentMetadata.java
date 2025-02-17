package ru.storage.content_metadata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import ru.storage.content_metadata.video.trailer.Trailer;
import ru.storage.content_metadata.video.standalone.StandaloneVideoShow;
import ru.storage.content_metadata.video.tv_series.TvSeries;
import ru.storage.person.filming_group.FilmingGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
  @JsonFormat(pattern = "dd.MM.yyyy")
  private LocalDate releaseDate;

  @DBRef
  @NotNull(message = "Необходимо указать страну")
  private Country country;

  @DBRef
  @NotNull(message = "Необходимо указать главный жанр")
  private Genre mainGenre;

  @DBRef private List<?> subGenres;
  private String description;

  @Min(0)
  @Max(21)
  @NotNull(message = "Необходимо указать возрастное ограничение")
  private Integer age;

  private Double rating;
  @DBRef private Poster poster;
  @DBRef private FilmingGroup filmingGroup;
  @DBRef private Trailer trailer;
  @DBRef private StandaloneVideoShow standaloneVideoShow;
  @DBRef private TvSeries tvSeries;
  @JsonIgnore @CreatedDate private LocalDateTime createdAt = LocalDateTime.now();

  public void setMainGenre(String name) {
    this.mainGenre = new Genre(name);
  }

  public void setMainGenre(Genre genre) {
    this.mainGenre = genre;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public void setCountry(String name) {
    this.country = new Country(name);
  }

  public void setSubGenres(List<String> subGenres) {
    this.subGenres = subGenres.stream().map(Genre::new).toList();
  }
}
