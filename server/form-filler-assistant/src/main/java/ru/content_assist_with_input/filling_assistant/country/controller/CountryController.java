package ru.content_assist_with_input.filling_assistant.country.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.content_assist_with_input.filling_assistant.common.ContentAssistController;
import ru.content_assist_with_input.filling_assistant.country.model.Country;
import ru.content_assist_with_input.filling_assistant.country.repo.CountryRepo;
import ru.content_assist_with_input.filling_assistant.country.service.CountryService;

import java.util.List;

@RestController
@RequestMapping("/fillingAssistants/countries")
public class CountryController {
    private final CountryService service;
    private final ContentAssistController<Country> commonController = new ContentAssistController<>(Country.class);

    @Autowired
    public CountryController(CountryService service) {
        this.service = service;
    }

    @PostMapping("/add/one")
    public ResponseEntity<String> addNewCountry(@RequestParam String countryName) {
        return commonController.addOne(countryName);
    }

    @PostMapping("/add/many")
    public ResponseEntity<String> addNewCountries(@RequestBody List<String> countryNames) {
        return commonController.addMany(countryNames);
    }

    @GetMapping("/get/bySequence")
    public List<String> findCountries(@RequestParam String sequence) {
        return service.findMatchedCountries(sequence);
    }

}
