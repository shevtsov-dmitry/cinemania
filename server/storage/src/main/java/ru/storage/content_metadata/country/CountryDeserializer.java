package ru.storage.content_metadata.country;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class CountryDeserializer extends JsonDeserializer<Country> {

  private final CountryRepo countryRepo;

  public CountryDeserializer(CountryRepo countryRepo) {
    this.countryRepo = countryRepo;
  }

  @Override
  public Country deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {
    String name = p.getText();
    var country = countryRepo.findByName(name);
    if (country.isEmpty()) {
      return countryRepo.save(new Country(name));
    } else {
      return countryRepo.findByName(name).get();
    }
  }
}
