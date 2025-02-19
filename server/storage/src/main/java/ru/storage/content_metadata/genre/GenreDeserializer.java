package ru.storage.content_metadata.genre;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class GenreDeserializer extends JsonDeserializer<Genre> {

  private final GenreRepo genreRepo;

  public GenreDeserializer(GenreRepo genreRepo) {
    this.genreRepo = genreRepo;
  }

  @Override
  public Genre deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {
    String name = p.getText();
    var genre = genreRepo.findByName(name);
    if (genre.isEmpty()) {
      return genreRepo.save(new Genre(name));
    } else {
      return genreRepo.findByName(name).get();
    }
  }
}
