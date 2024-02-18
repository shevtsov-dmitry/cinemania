package ru.filling_assistant.country;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.filling_assistant.common.BaseService;

import java.util.List;

@Service
public class CountryService extends BaseService<Country> {

    private final CountryRepo repo;
    private final Pageable foundCountryAmountRestriction = PageRequest.of(0, 5);

    @Autowired
    public CountryService(CountryRepo repo) {
        super(repo);
        this.repo = repo;
    }

    @Override
    public Country save(Country entity) {
        return super.save(entity);
    }

    public List<String> findMatchedCountries(String sequence) {
        return repo.getCountryNamesBySimilarStringSequence(sequence, foundCountryAmountRestriction);
    }

    @Override
    public List<Country> saveWithoutDuplicates(List<Country> receivedEntities) {
        return super.saveWithoutDuplicates(receivedEntities);
    }
    @Transactional
    public ResponseEntity<String> deleteCountries(List<String> countryNamesToDelete) {
        return super.deleteEntitiesByName(countryNamesToDelete);
    }
}
