package ru.storage.filling_assistant.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.storage.filling_assistant.common.BaseService;

import java.util.List;

@Service
public class CountryService extends BaseService<Country> {

    private final CountryRepo repo;

    @Autowired
    public CountryService(CountryRepo repo) {
        super(repo);
        this.repo = repo;
    }

    @Override
    public Country save(Country entity) {
        return super.save(entity);
    }

    @Override
    public List<Country> saveWithoutDuplicates(List<Country> receivedEntities) {
        return super.saveWithoutDuplicates(receivedEntities);
    }

    public List<Country> getAllCountries() {
        return super.getAllEntities();
    }

    public ResponseEntity<String> deleteCountries(List<String> countryNamesToDelete) {
        return super.deleteEntitiesByName(countryNamesToDelete);
    }
}
