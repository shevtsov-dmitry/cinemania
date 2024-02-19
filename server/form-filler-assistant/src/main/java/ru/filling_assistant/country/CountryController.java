package ru.filling_assistant.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.filling_assistant.common.BaseController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fillingAssistants/countries")
public class CountryController extends BaseController<Country> {

    private final CountryService service;

    @Autowired
    public CountryController(CountryService service) {
        super(service);
        this.service = service;
    }

    @PostMapping("/add/one")
    public ResponseEntity<String> addNewCountry(@RequestParam String name) {
        return super.tryToSaveOneEntity(new Country(name));
    }

    @PostMapping("/add/many")
    public ResponseEntity<List<Long>> addNewCountries(@RequestBody List<String> countryNames) {
        List<Country> countries = new ArrayList<>(countryNames.size());
        countryNames.forEach(name -> countries.add(new Country(name)));
        return super.tryToSaveListOfEntities(countries);
    }

    @GetMapping(value = "/get/bySequence", produces = "application/json;charset=UTF-8")
    public List<String> findCountries(@RequestParam String sequence) {
        return service.findMatchedCountries(sequence);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRequestedCountries(@RequestBody List<String> countryNames) {
        return service.deleteCountries(countryNames);
    }

}
