package ru.storage.metadata;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.storage.utility.HttpHeaderHelpers;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v0/metadata")
public class MetadataController {

    private final MetadataService service;

    public MetadataController(MetadataService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> saveFormData(VideoInfoPartsTuple metadataObjects) {
        service.saveMetadata(metadataObjects);
    }

//    @GetMapping(value = "title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(@PathVariable String title) {
//        return service.getMetadataByTitle(title);
//    }

}
