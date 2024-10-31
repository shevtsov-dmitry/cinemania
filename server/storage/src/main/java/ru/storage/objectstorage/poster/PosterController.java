package ru.storage.objectstorage.poster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.objectstorage.poster.exceptions.CustomNumberFormatException;
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

    private static final Logger LOG = LoggerFactory.getLogger(PosterController.class);

    private final PosterService service;

    public PosterController(PosterService service) {
        this.service = service;
    }

    /**
     * Saves poster into S3 and its metadata into local db
     *
     * @param metadataId - id of existing video content metadata
     * @param image      - multipart file of image type
     * @return Response:
     * <ul>
     *     <li>201 (CREATED)</li>
     *     <li>400 (BAD_REQUEST) with the cause header "Message"</li>
     *     <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message"</li>
     * </ul>
     */
    @PostMapping("/upload")
    public ResponseEntity<Poster> savePoster(@RequestParam Long metadataId, @RequestParam MultipartFile image) {
        HttpHeaders headers = new HttpHeaders();
        if (!Objects.requireNonNull(image.getContentType()).startsWith("image")) {
            writeEncodedMessageHeader(headers, "Ошибка при сохранении постера. Файл не является изображением.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

        try {
            final Poster savedPosterMetadata = service.savePoster(metadataId, image);
            return new ResponseEntity<>(savedPosterMetadata, HttpStatus.CREATED);
        } catch (InvalidDataAccessApiUsageException e) {
            writeEncodedMessageHeader(headers, e.getMessage());
            LOG.warn(e.getMessage());
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        } catch (UncheckedIOException e) {
            writeEncodedMessageHeader(headers, "Ошибка при сохранении постера для видео.");
            LOG.warn(e.getMessage());
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves poster images from S3 storage based on specified metadata IDs.
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
        } catch (CustomNumberFormatException e) {
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
     * Replaces existing poster with a new one.
     *
     * @param metadataId - content metadata id
     * @param image      - multipart file of image type
     * @return Response:
     * <ul>
     *     <li>200 (OK) with the success header "Message"</li>
     *     <li>400 (BAD_REQUEST) with the success header "Message"</li>
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
        } catch (Exception e) {
            writeEncodedMessageHeader(headers, "Неудалось заменить существующий постер.");
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes saved poster which matches requested ids from S3 and local db.
     * <p>
     * Also supports single id instance. example: {@code "4,2,592,101,10"}.
     * </p>
     *
     * @param contentMetadataIds - ids split by ',' separator.
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
        } catch (CustomNumberFormatException e) {
            writeEncodedMessageHeader(headers, e.getMessage());
            LOG.warn(e.getMessage());
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        } catch (S3Exception e) {
            String errMessage = "Ошибка при удалении постеров по их идентификаторам.";
            writeEncodedMessageHeader(headers, errMessage);
            LOG.warn("%s %s".formatted(errMessage, e.getMessage()));
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
