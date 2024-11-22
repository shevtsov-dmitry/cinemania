package ru.storage.metadata.objectstorage.poster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.metadata.objectstorage.exceptions.NoMetadataRelationException;
import ru.storage.metadata.objectstorage.exceptions.ParseRequestIdException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.storage.utility.HttpHeaderHelpers.writeEncodedMessageHeader;

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
     *     <li>400 (BAD_REQUEST) with the cause header "Message"
     *          <ol>
     *              <li>when invalid poster arguments</li>
     *              <li>when try to save without content metadata relation</li>
     *          </ol>
     *     </li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<Poster> saveMetadata(@RequestBody Poster poster) {
        try {
            final Poster savedPosterMetadata = service.saveMetadata(poster);
            return new ResponseEntity<>(savedPosterMetadata, HttpStatus.CREATED);
        } catch (NoMetadataRelationException | IllegalArgumentException e) {
            HttpHeaders headers = new HttpHeaders();
            writeEncodedMessageHeader(headers, e.getMessage());
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Upload poster into S3 cloud storage
     *
     * @param posterMetadataId id of poster metadata from db
     * @param image            multipart file of image type
     * @return Response
     * <ul>
     *     <li>201 (CREATED)</li>
     *     <li>400 (BAD_REQUEST) when invalid args</li>
     *     <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message" when image wasn't saved into S3 cloud storage</li>
     * </ul>
     */
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadImage(@RequestParam Long posterMetadataId, @RequestParam MultipartFile image) {
        HttpHeaders headers = new HttpHeaders();

        try {
            service.uploadImage(posterMetadataId, image);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            writeEncodedMessageHeader(headers, e.getMessage());
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        } catch (S3Exception e) {
            writeEncodedMessageHeader(headers, e.getMessage());
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieve poster images from S3 cloud storage based on specified metadata IDs.
     *
     * <p>This method supports both single and multiple content metadata IDs, separated by commas.
     * For example, {@code "4,2,592,101,10"}.</p>
     *
     * @param contentMetadataIds a comma-separated string of content metadata IDs
     * @return Response.
     * <ul>
     *     <li>200 (OK). A list of byte arrays representing the images if successful .
     *     The values are in the same order as requested ids.</li>
     *     <li>400 (BAD_REQUEST). For inputs that are not string, also if number format is incorrect</li>
     *     <li>500 (INTERNAL_SERVER_ERROR). An empty list with an error message header if an error occurs </li>
     * </ul>
     */
    @GetMapping("/images/{contentMetadataIds}")
    public ResponseEntity<List<byte[]>> getImagesByMetadataId(@PathVariable String contentMetadataIds) {
        try {
            return ResponseEntity.ok(service.getImagesMatchingMetadataIds(contentMetadataIds));
        } catch (ParseRequestIdException e) {
            HttpHeaders headers = new HttpHeaders();
            writeEncodedMessageHeader(headers, e.getMessage());
            LOG.warn(e.getMessage());
            return new ResponseEntity<>(Collections.emptyList(), headers, HttpStatus.BAD_REQUEST);
        } catch (UncheckedIOException e) {
            HttpHeaders headers = new HttpHeaders();
            writeEncodedMessageHeader(headers, "Ошибка при поиске плакатов.");
            LOG.warn(e.getMessage());
            return new ResponseEntity<>(Collections.emptyList(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Replace existing poster with a new one.
     *
     * @param metadataId content metadata id
     * @param image      multipart file of image type
     * @return Response:
     * <ul>
     *     <li>200 (OK) with the success header "Message"</li>
     *     <li>400 (BAD_REQUEST) with the cause header "Message"</li>
     *     <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message"</li>
     * </>
     */
    @PutMapping("/change")
    public ResponseEntity<Void> updateExistingImage(@RequestParam Long metadataId, @RequestParam MultipartFile image) {
        HttpHeaders headers = new HttpHeaders();
        final String contentType = Optional.ofNullable(image.getContentType()).orElse("image/jpeg");
        if (!contentType.startsWith("image/")) {
            writeEncodedMessageHeader(headers, "Постер успешно заменён на новый.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

        try {
            service.updateExistingImage(metadataId, image);
            writeEncodedMessageHeader(headers, "Постер успешно заменён на новый.");
            return new ResponseEntity<>(null, headers, HttpStatus.OK);
        } catch (NoMetadataRelationException e) {
            String errmes = e.getMessage();
            LOG.warn(errmes);
            writeEncodedMessageHeader(headers, errmes);
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        } catch (UncheckedIOException e) {
            String errmes = "Не удалось заменить существующий постер.";
            LOG.warn("%s %s".formatted(errmes, e.getMessage()));
            writeEncodedMessageHeader(headers, errmes);
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete saved poster which matches requested ids from S3 cloud storage and local db.
     * <p>
     * Also supports single id instance. example: {@code "4,2,592,101,10"}.
     * </p>
     *
     * @param contentMetadataIds ids split by ',' separator.
     * @return Response:
     * <ul>
     *      <li>200 (OK)</li>
     *      <li>500 (INTERNAL_SERVER_ERROR)</li>
     * </ul>
     */
    @DeleteMapping("/ids/{contentMetadataIds}")
    public ResponseEntity<Void> deletePostersByIds(@PathVariable String contentMetadataIds) {
        HttpHeaders headers = new HttpHeaders();
        try {
            service.deleteByIds(contentMetadataIds);
            writeEncodedMessageHeader(headers, "Выбранные постеры успешно удалены");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ParseRequestIdException e) {
            writeEncodedMessageHeader(headers, e.getMessage());
            LOG.warn(e.getMessage());
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        } catch (S3Exception e) {
            String errmes = "Ошибка при удалении постеров по их идентификаторам.";
            writeEncodedMessageHeader(headers, errmes);
            LOG.warn("%s %s".formatted(errmes, e.getMessage()));
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
