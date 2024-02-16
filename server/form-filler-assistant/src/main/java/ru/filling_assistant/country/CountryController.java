package ru.filling_assistant.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.filling_assistant.common.ContentAssistController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fillingAssistants/countries")
public class CountryController extends ContentAssistController<Country> {
    private final CountryService service;

    @Autowired
    public CountryController(CountryService service) {
        super(service);
        this.service = service;
    }

    @PostMapping("/add/one")
    public ResponseEntity<String> addNewCountry(@RequestParam String countryName) {
        return super.tryToSaveOneEntity(new Country(countryName));
    }

    @PostMapping("/add/many")
    public ResponseEntity<List<String>> addNewCountries(@RequestBody List<String> countryNames) {
        List<Country> countries = new ArrayList<>(countryNames.size());
        countryNames.forEach(name -> countries.add(new Country(name)));
        return super.tryToSaveListOfEntities(countries);
    }

    @GetMapping("/get/bySequence")
    public List<String> findCountries(@RequestParam String sequence) {
        return service.findMatchedCountries(sequence);
    }

}
