package ru.storage.content_metadata.country;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@Document
public class Country {
  @Id private String id;

  @Indexed(unique = true)
  private String name;

  private Map<String, String> localizedNames;

  public Country(String name) {
    this.name = name;
  }
}
