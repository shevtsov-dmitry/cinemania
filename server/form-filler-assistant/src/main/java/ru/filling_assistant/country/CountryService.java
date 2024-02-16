package ru.filling_assistant.country;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.filling_assistant.common.ContentAssistService;

import java.util.List;

@Service
public class CountryService extends ContentAssistService<Country> {

    private final CountryRepo repo;
    private final Pageable foundCountryAmountRestriction = PageRequest.of(0, 5);

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
}
