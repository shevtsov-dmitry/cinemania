package ru.storage.metadata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.storage.metadata.objectstorage.exceptions.NoMetadataRelationException;

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
     *     <li>400 (BAD REQUEST)
     *         <ol>
     *             <li>when some of the instances doesn't have linked field</li>
     *             <li>when content type is wrong</li>
     *         </ol>
     *     </li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<VideoInfoParts> saveFormData(VideoInfoParts metadataObjects) {
        try {
            final var savedVideoInfoParts = service.saveMetadata(metadataObjects);
            return new ResponseEntity<>(savedVideoInfoParts, HttpStatus.CREATED);
        } catch (NoMetadataRelationException | IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping(value = "title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(@PathVariable String title) {
//        return service.getMetadataByTitle(title);
//    }

}
