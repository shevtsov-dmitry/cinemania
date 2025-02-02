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
public class StandaloneVideoShow {
  @Id private String id;
  private String filename;
  private String contentType;

  public StandaloneVideoShow(String filename, String contentType) {
    this.filename = filename;
    this.contentType = contentType;
  }

}
