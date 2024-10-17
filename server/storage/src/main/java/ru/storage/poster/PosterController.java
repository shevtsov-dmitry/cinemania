package ru.storage.poster;

import java.io.IOException;
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
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        try {
            String savedPosterId = service.save(file);
            return new ResponseEntity<>(savedPosterId, httpHeaders, HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(
                    "Impossible to read video file.%n Reason: %s".formatted(ex.getMessage()), httpHeaders,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(
                    "Couldn't save video. Video content is empty.%n Reason: %s".formatted(ex.getMessage()), httpHeaders,
                    HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(
                    "Couldn't save the video on a server. %n Reason: %s".formatted(e.getMessage()), httpHeaders,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/get/recent/{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, byte[]>>> getRecentlySavedPosters(@PathVariable int amount) {
        return ResponseEntity.ok(service.getRecentlySavedPosters(amount));
    }

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
