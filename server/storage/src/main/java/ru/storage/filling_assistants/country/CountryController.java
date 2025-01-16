package ru.storage.filling_assistants.country;

import com.mongodb.MongoException;

import ru.storage.utils.EncodedHttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v0/filling-assistants/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@RequestBody Country country) {
        try {
            countryService.save(country);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (MongoException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "multiple", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@RequestBody List<Country> countries) {
        try {
            countryService.save(countries);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (MongoException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllNames() {
        return new ResponseEntity<>(countryService.getAll(), HttpStatus.OK);
    }

    @DeleteMapping("{name}")
    public ResponseEntity<Void> deleteByName(@PathVariable String name) {
        countryService.deleteByName(name);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
