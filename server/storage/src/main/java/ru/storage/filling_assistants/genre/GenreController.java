package ru.storage.filling_assistants.genre;

import com.mongodb.MongoException;

import ru.storage.utils.EncodedHttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v0/filling-assistants/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@RequestBody Genre genre) {
        try {
            genreService.save(genre);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (MongoException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "multiple", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@RequestBody List<Genre> genres) {
        try {
            genreService.save(genres);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (MongoException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllNames() {
        return new ResponseEntity<>(genreService.getAll(), HttpStatus.OK);
    }

    @DeleteMapping("{name}")
    public ResponseEntity<Void> deleteByName(@PathVariable String name) {
        genreService.deleteByName(name);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
