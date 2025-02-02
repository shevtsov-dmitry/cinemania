package ru.storage.content_metadata.video;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Episode {

  @Id private String id;
  private String filename;
  private String contentType;
  private Integer season;
  private Integer episode;

  public Episode(String filename, String contentType, Integer season, Integer episode) {
    this.filename = filename;
    this.contentType = contentType;
    this.season = season;
    this.episode = episode;
  }
}
