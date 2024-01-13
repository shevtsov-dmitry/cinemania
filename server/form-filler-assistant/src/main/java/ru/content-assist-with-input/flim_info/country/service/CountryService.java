package com.content_assist_with_input.flim_info.country.service;

import com.content_assist_with_input.flim_info.common.BaseContentAssistService;
import com.content_assist_with_input.flim_info.country.model.Country;
import com.content_assist_with_input.flim_info.country.repo.CountryRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService extends BaseContentAssistService<Country> {

    private final CountryRepo repo;
    private final Pageable foundCountryAmountRestriction = PageRequest.of(0, 5);

    public CountryService(CrudRepository<Country, Long> repo, CountryRepo repo1) {
        super(repo);
        this.repo = repo1;
    }

    public List<String> findMatchedCountries(String sequence) {
        return repo.getCountryNamesBySimilarStringSequence(sequence, foundCountryAmountRestriction);
    }

    @Override
    public String saveWithoutDuplicates(List<Country> receivedEntities) {
        return super.saveWithoutDuplicates(receivedEntities);
    }
}
