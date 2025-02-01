package ru.storage.content_metadata.video;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
@Builder
public class Video {
  @Id private String id;
  private String filename;
  private String contentType;
  @Nullable private Integer season;
  @Nullable private Integer episode;

  public Video(String filename, String contentType) {
    this.filename = filename;
    this.contentType = contentType;
  }

  public Video(String filename, String contentType, int season, int episode) {
    this.filename = filename;
    this.contentType = contentType;
    this.season = season;
    this.episode = episode;
  }

}
