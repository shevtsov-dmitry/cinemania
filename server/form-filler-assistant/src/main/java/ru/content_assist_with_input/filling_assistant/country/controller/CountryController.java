package ru.content_assist_with_input.filling_assistant.country.controller;

import ru.content_assist_with_input.filling_assistant.common.ContentAssistController;
import ru.content_assist_with_input.filling_assistant.country.model.Country;
import ru.content_assist_with_input.filling_assistant.country.repo.CountryRepo;
import ru.content_assist_with_input.filling_assistant.country.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fillingAssistants/countries")
public class CountryController {
    private final CountryService service;
    private final CountryRepo repo;

    @Autowired
    public CountryController(CountryService service, CountryRepo repo) {
        this.service = service;
        this.repo = repo;
    }

    @PostMapping("/add/one")
    public String addNewCountry(@RequestParam String countryName) {
        Country country = new Country(countryName);
        try {
            if (country.getName().equals("") || country.getName().equals(" ")) {
                return "incoming parameter data is empty.";
            }
            repo.save(country);
            return "new country was added successfully.";
        } catch (Exception e) {
            return service.saveWithoutDuplicates(new ArrayList<>(List.of(country))).toString();
        }
    }

    @PostMapping("/add/many")
    public String addNewCountries(@RequestBody Map<String, List<Country>> jsonMap) {
        List<Country> countries = jsonMap.get("countries");
        try {
            countries.removeIf(genre -> genre.getName().equals("") || genre.getName().equals(" "));
            repo.saveAll(countries);
            return "new countries have been added successfully.";
        } catch (Exception e) { // DataIntegrityViolationException
            return service.saveWithoutDuplicates(countries).toString();
        }
    }

    @GetMapping("/get/many/by-sequence")
    public List<String> findCountries(@RequestParam String sequence) {
        return service.findMatchedCountries(sequence);
    }

}
