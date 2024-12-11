package ru.storage.metadata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.storage.utility.EncodedHttpHeaders;

import java.util.List;

@RestController
@RequestMapping("/api/v0/metadata")
public class MetadataController {

    private final MetadataService service;

    public MetadataController(MetadataService service) {
        this.service = service;
    }

    /**
     * @param metadataObjects {@link VideoInfoParts} objects record
     * @return Response
     * <ul>
     *     <li>201 (CREATED)</li>
     *     <li>400 (BAD REQUEST) when content type is wrong </li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<ContentMetadata> saveFormData(VideoInfoParts metadataObjects) {
        try {
            final var savedContentMetadata = service.saveMetadata(metadataObjects);
            return new ResponseEntity<>(savedContentMetadata, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get recently added list of metadata {@link ContentMetadata}.
     *
     * @param amount requested amount
     * @return Response
     * <ul>
     *     <li>200 (OK)</li>
     * </ul>
     */
    @GetMapping("/recent/{amount}")
    public ResponseEntity<List<ContentMetadata>> getRecentlyAdded(@PathVariable int amount) {
        return ResponseEntity.ok(service.getRecentlyAdded(amount));
    }

//    @GetMapping(value = "title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(@PathVariable String title) {
//        return service.getMetadataByTitle(title);
//    }

}
