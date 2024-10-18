package ru.storage.objectstorage.poster;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/posters")
public class PosterController {

    private final PosterService service;

    @Autowired
    public PosterController(PosterService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> savePoster(@RequestParam MultipartFile file) {
        // TODO upload into S3
        return null;
    }

    // @GetMapping(value = "/get/recent/{amount}", produces =
    // MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<List<Map<String, byte[]>>>
    // getRecentlySavedPosters(@PathVariable int amount) {
    // return ResponseEntity.ok(service.getRecentlySavedPosters(amount));
    // }

    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> deletePosterById(@PathVariable String id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        return service.deleteById(id)
                ? new ResponseEntity<>("poster image with id %s successfully deleted.".formatted(id),
                        httpHeaders, HttpStatus.OK)
                : ResponseEntity.notFound().build();
    }

}
