package ru.storage.content_metadata.genre;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenreArrayDeserializer extends JsonDeserializer<List<Genre>> {

  private final GenreRepo repo;

  public GenreArrayDeserializer(GenreRepo repo) {
    this.repo = repo;
  }

  @Override
  public List<Genre> deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    ArrayNode arrayNode = jsonParser.getCodec().readTree(jsonParser);
    List<Genre> genres = new ArrayList<>(arrayNode.size());
    arrayNode.forEach(
        item -> {
          if (repo.findByName(item.asText()).isEmpty()) {
            genres.add(repo.save(new Genre(item.asText())));
          } else {
            genres.add(repo.findByName(item.asText()).get());
          }
        });
    return genres;
  }
}
