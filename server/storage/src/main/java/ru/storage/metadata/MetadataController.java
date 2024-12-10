package ru.storage.metadata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/metadata")
public class MetadataController {

    private final MetadataService service;

    public MetadataController(MetadataService service) {
        this.service = service;
    }

    /**
     * @param metadataObjects {@link VideoInfoParts} record of required metadata
     * @return Response
     * <ul>
     *     <li>201 (CREATED)</li>
     *     <li>400 (BAD REQUEST) when content type is wrong </li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<VideoInfoParts> saveFormData(VideoInfoParts metadataObjects) {
        try {
            final var savedVideoInfoParts = service.saveMetadata(metadataObjects);
            return new ResponseEntity<>(savedVideoInfoParts, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping(value = "title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(@PathVariable String title) {
//        return service.getMetadataByTitle(title);
//    }

}
