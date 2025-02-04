package ru.storage.content_metadata.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trailer {
  @Id private String id;
  private String filename;
  private String contentType;
  private Long size;

  public Trailer(String filename, String contentType, Long size) {
    this.filename = filename;
    this.contentType = contentType;
    this.size = size;
  }
}
