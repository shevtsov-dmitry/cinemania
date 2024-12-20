package ru.storage.content;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.storage.utility.EncodedHttpHeaders;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v0/metadata")
public class ContentController {

    private final ContentService service;

    public ContentController(ContentService service) {
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
    public ResponseEntity<ContentDetails> saveFormData(@RequestBody VideoInfoParts metadataObjects) {
        try {
            return new ResponseEntity<>(null, new EncodedHttpHeaders("приветствую на первом совещании связанном с развитием союзного государства"), HttpStatus.BAD_REQUEST);
            // final var savedContentMetadata = service.saveMetadata(metadataObjects);
            // return new ResponseEntity<>(savedContentMetadata, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get recently added list of metadata {@link ContentDetails}.
     *
     * @param amount requested amount
     * @return Response
     * <ul>
     *     <li>200 (OK)</li>
     * </ul>
     */
    @GetMapping("recent/{amount}")
    public ResponseEntity<List<ContentDetails>> getRecentlyAdded(@PathVariable int amount) {
        return ResponseEntity.ok(service.getRecentlyAdded(amount));
    }

    /**
     * Delete all content related instances from local metadata db and also from S3 storage.
     *
     * @param contentId contentId from local db
     * @return Response
     * <ul>
     *     <li>204 (NO_CONTENT) on success</li>
     *     <li>400 (BAD_REQUEST) when error on parsing or passed illegal arguments</li>
     *     <li>404 (NOT_FOUND) when there is no such element related to requested id</li>
     *     <li>500 (INTERNAL_SERVER_ERROR) when S3 couldn't remove content for some reason</li>
     * <ul/>
     */
    @DeleteMapping("{contentId}")
    public ResponseEntity<ContentDetails> remove(@PathVariable String contentId) {
        try {
            service.removeContent(contentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (S3Exception e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
