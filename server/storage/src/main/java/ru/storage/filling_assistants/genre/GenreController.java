package ru.storage.filling_assistants.genre;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.storage.filling_assistants.base.BaseController;

import java.util.List;

@RestController
@RequestMapping("api/v0/filling-assistants/genres")
public class GenreController {

    private final BaseController<Genre> baseController;

    public GenreController(BaseController<Genre> baseController) {
        this.baseController = baseController;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@RequestBody Genre genre) {
        return baseController.save(genre);
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
