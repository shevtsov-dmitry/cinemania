package ru.storage.content_metadata;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.storage.utils.EncodedHttpHeaders;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@RequestMapping("api/v0/metadata")
public class ContentMetadataController {

    private final ContentMetadataService service;

    public ContentMetadataController(ContentMetadataService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContentMetadata> saveFormData(@RequestBody ContentMetadataDTO metadataDTO) {
        try {
            final var savedContentMetadata = service.saveMetadataFromDTO(metadataDTO);
            return new ResponseEntity<>(savedContentMetadata, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("genre/{encodedName}/{amount}")
    public ResponseEntity<List<ContentMetadata>> getMetadataByGenre(@PathVariable String encodedName,
            @Nullable @PathVariable Integer amount) {
        if (amount == null || amount <= 0) {
            amount = 10;
        }
        String name = URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
        return ResponseEntity.ok(service.getMetadataByGenre(name, amount));

    }

    /**
     * Get recently added list of metadata {@link ContentMetadata}.
     *
     * @param amount requested amount
     * @return Response
     *         <ul>
     *         <li>200 (OK)
     *         </ul>
     */
    @GetMapping("recent/{amount}")
    public ResponseEntity<List<ContentMetadata>> getRecentlyAdded(@PathVariable int amount) {
        return ResponseEntity.ok(service.getRecentlyAdded(amount));
    }

    @GetMapping("byId/{id}")
    public ResponseEntity<ContentMetadata> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(null,
                        new EncodedHttpHeaders("Запрошенный видео файл не найден."), HttpStatus.NOT_FOUND));
    }

    /**
     * Delete all content related instances from local metadata db and also from S3
     * storage.
     *
     * @param contentId contentId from local db
     * @return Response
     *         <ul>
     *         <li>204 (NO_CONTENT) on success
     *         <li>400 (BAD_REQUEST) when error on parsing or passed illegal
     *         arguments
     *         <li>404 (NOT_FOUND) when there is no such element related to
     *         requested id
     *         <li>500 (INTERNAL_SERVER_ERROR) when S3 couldn't remove content for
     *         some reason
     *         <ul/>
     */
    @DeleteMapping("{contentId}")
    public ResponseEntity<ContentMetadata> remove(@PathVariable String contentId) {
        try {
            service.removeContent(contentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(
                    null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (S3Exception e) {
            return new ResponseEntity<>(
                    null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
