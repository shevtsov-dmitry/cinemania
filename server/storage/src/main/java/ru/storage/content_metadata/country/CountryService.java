package ru.storage.content_metadata.country;

import com.mongodb.MongoException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CountryService {

  private static final Logger log = LoggerFactory.getLogger(CountryService.class);
  private final CountryRepo countryRepo;

  public CountryService(CountryRepo countryRepo) {
    this.countryRepo = countryRepo;
  }

  void save(Country country) {
    try {
      countryRepo.save(country);
    } catch (Exception e) {
      String errmes = "Ошибка при сохранении новой страны в бд.";
      log.warn("{} {}", errmes, e.getMessage());
      throw new MongoException(errmes);
    }
  }

  void save(List<String> countryNames) {
    try {
      List<Country> countries = countryNames.stream().map(Country::new).toList();
      countryRepo.saveAll(countries);
    } catch (Exception e) {
      String errmes = "Ошибка при сохранении новых стран в бд.";
      log.error("{} {}", errmes, e);
      throw new MongoException(errmes);
    }
  }

  List<String> getAll() {
    return countryRepo.findAll().stream().map(Country::getName).toList();
  }

  void deleteByName(String name) {
    countryRepo.deleteByName(name);
  }
}
