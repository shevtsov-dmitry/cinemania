package ru.storage.filling_assistants.country;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.storage.filling_assistants.base.BaseController;

import java.util.List;

@RestController
@RequestMapping("api/v0/filling-assistants/countries")
public class CountryController {

    private final BaseController<Country> baseController;

    public CountryController(BaseController<Country> baseController) {
        this.baseController = baseController;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@RequestBody Country country) {
        return baseController.save(country);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllNames() {
        return baseController.getAll();
    }

    @DeleteMapping("{name}")
    public ResponseEntity<Void> deleteByName(@PathVariable String name) {
        return baseController.deleteByName(name);
    }

}
