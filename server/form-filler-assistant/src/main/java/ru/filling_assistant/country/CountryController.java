package ru.filling_assistant.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.filling_assistant.common.ContentAssistController;

import java.util.List;

@RestController
@RequestMapping("/fillingAssistants/countries")
public class CountryController {
    private final CountryService service;
    private final ContentAssistController<Country> commonController;

    @Autowired
    public CountryController(Class<Country> countryClass, CountryService service) {
        this.service = service;
        commonController = new ContentAssistController<>(countryClass, service);
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
