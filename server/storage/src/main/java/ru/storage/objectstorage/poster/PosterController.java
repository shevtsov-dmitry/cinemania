package ru.storage.objectstorage.poster;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static ru.storage.utility.HttpHeaderHelpers.writeMessageHeader;

@RestController
@RequestMapping("/api/v1/posters")
public class PosterController {

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
        if (!image.getContentType().startsWith("image")) {
            writeMessageHeader(headers, "Ошибка при сохранении постера. Файл не является изображением.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

        try {
            final Poster savedPopsterMetadata = service.savePoster(metadataId, image);
            return new ResponseEntity<>(savedPopsterMetadata, HttpStatus.CREATED);
        } catch (Exception e) {
            writeMessageHeader(headers, "Ошибка при сохранении постера для видео.");
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
     *     <li>200 (OK). A list of byte arrays representing the images if successful . The values are in the same order as requested ids.</li>
     *     <li>500 (INTERNAL_SERVER_ERROR). An empty list with an error message header if an error occurs </li>
     * </ul>
     */
    @GetMapping("/images/{contentMetadataIds}")
    public ResponseEntity<List<byte[]>> getImagesByContentMetadataId(@PathVariable String contentMetadataIds) {
        try {
            return ResponseEntity.ok(service.getImagesByContentMetadataId(contentMetadataIds));
        } catch (Exception e) {
            HttpHeaders headers = new HttpHeaders();
            writeMessageHeader(headers, "Ошибка при поиске плакатов.");
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
     *     <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message"</li>
     * </>
     */
    @PutMapping("/change")
    public ResponseEntity<Void> updateExistingImage(@RequestParam Long metadataId, @RequestParam MultipartFile image) {
        HttpHeaders headers = new HttpHeaders();
        if (!image.getContentType().startsWith("image")) {
            writeMessageHeader(headers, "Постер успешно заменён на новый.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

        try {
            service.updateExistingImage(metadataId, image);
            writeMessageHeader(headers, "Постер успешно заменён на новый.");
            return new ResponseEntity<>(null, headers, HttpStatus.OK);
        } catch (Exception e) {
            writeMessageHeader(headers, "Неудалось заменить существующий постер.");
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
            writeMessageHeader(headers, "Выбранные постеры успешно удалены");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            writeMessageHeader(headers, "Ошибка при удалении постеров по их идентификаторам.");
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
