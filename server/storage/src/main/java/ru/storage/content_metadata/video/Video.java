package ru.storage.content_metadata.video;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Video {
  @Id private String id;
  private String filename;
  private String contentType;
  @Nullable private Integer season;
  @Nullable private Integer episode;
}
