package ru.storage.content_metadata.genre;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class Genre {
  @Id private String id;

  @Indexed(unique = true)
  private String name;

  private Map<String, String> localizedNames;

  public Genre(String name) {
    this.name = name;
  }
}
