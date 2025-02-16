package ru.storage.person.filming_group;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.person.content_creator.ContentCreator;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmingGroup {
  @Id private String id;
  private ContentCreator director;
  private List<ContentCreator> actors;
}
