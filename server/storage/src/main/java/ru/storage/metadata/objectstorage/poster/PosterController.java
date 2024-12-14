package ru.storage.metadata.objectstorage.poster;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.metadata.objectstorage.exceptions.ParseRequestIdException;
import ru.storage.utility.EncodedHttpHeaders;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v0/posters")
public class PosterController {

    private final PosterService service;

    public PosterController(PosterService service) {
        this.service = service;
    }

    /**
     * Save poster metadata into db.
     *
     * @param poster poster instance
     * @return Response
     * <ul>
     *     <li>201 (CREATED) with body of saved Poster instance</li>
     *     <li>400 (BAD_REQUEST) with the cause header "Message" when invalid arguments</li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<Poster> saveMetadata(@RequestBody Poster poster) {
        try {
            final Poster savedPosterMetadata = service.saveMetadata(poster);
            return new ResponseEntity<>(savedPosterMetadata, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Upload poster into S3 cloud storage
     *
     * @param id    poster metadata id from mongodb db
     * @param image multipart file of image type
     * @return Response
     * <ul>
     *     <li>201 (CREATED)</li>
     *     <li>400 (BAD_REQUEST) with the cause header "Message" when invalid args</li>
     *     <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message" when image wasn't saved into S3 cloud storage</li>
     * </ul>
     */
    @PostMapping("upload")
    public ResponseEntity<Void> uploadImage(@RequestParam String id, @RequestParam MultipartFile image) {
        try {
            service.uploadImage(id, image);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (S3Exception e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieve poster images from S3 cloud storage based on specified metadata IDs.
     *
     * <p>This method supports both single and multiple content metadata IDs, separated by commas.
     *
     * @param contentMetadataIds a comma-separated string of content metadata IDs
     * @return Response
     * <ul>
     *     <li>200 (OK). A list of byte arrays representing the images if successful .
     *     The values are in the same order as requested ids.</li>
     *     <li>400 (BAD_REQUEST). For inputs that are not string, also if number format is incorrect</li>
     *     <li>500 (INTERNAL_SERVER_ERROR). An empty list with an error message header if an error occurs </li>
     * </ul>
     */
    @GetMapping("{contentMetadataIds}")
    public ResponseEntity<List<byte[]>> getImagesByMetadataId(@PathVariable String contentMetadataIds) {
        try {
            return ResponseEntity.ok(service.getImagesMatchingMetadataIds(contentMetadataIds));
        } catch (ParseRequestIdException e) {
            return new ResponseEntity<>(Collections.emptyList(),
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (S3Exception e) {
            return new ResponseEntity<>(Collections.emptyList(),
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete saved poster which matches requested ids from S3 cloud storage and local db.
     * <p>
     * Also supports single id instance.
     * </p>
     *
     * @param contentMetadataIds ids split by ',' separator.
     * @return Response
     * <ul>
     *      <li>204 (NO_CONTENT)</li>
     *      <li>500 (INTERNAL_SERVER_ERROR)</li>
     * </ul>
     */
    @DeleteMapping("{contentMetadataIds}")
    public ResponseEntity<Void> deletePostersByIds(@PathVariable String contentMetadataIds) {
        try {
            service.deleteByIds(contentMetadataIds);
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders("Выбранные постеры успешно удалены."),
                    HttpStatus.NO_CONTENT);
        } catch (ParseRequestIdException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (S3Exception e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
