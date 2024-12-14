package ru.storage.metadata.objectstorage.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.metadata.objectstorage.exceptions.ParseRequestIdException;
import ru.storage.utility.EncodedHttpHeaders;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@RequestMapping("/api/v0/videos")
public class VideoController {
    private final VideoService service;

    @Autowired
    public VideoController(VideoService service) {
        this.service = service;
    }

    /**
     * Save video metadata into db.
     *
     * @param video video instance
     * @return Response
     * <ul>
     *     <li>201 (CREATED) with body of saved video instance</li>
     *     <li>400 (BAD_REQUEST) with the cause header "Message" when invalid arguments </li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<Video> saveMetadata(@RequestBody Video video) {
        try {
            final Video savedPosterMetadata = service.saveMetadata(video);
            return new ResponseEntity<>(savedPosterMetadata, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Upload video into S3 cloud storage by chunks
     *
     * @param id    video metadata id from mongodb
     * @param video multipart file of image type
     * @return Response
     * <ul>
     *     <li>204 (NO_CONTENT)</li>
     *     <li>400 (BAD_REQUEST) when invalid args</li>
     *     <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message" when video wasn't saved into S3 cloud storage</li>
     * </ul>
     */
    @PostMapping("upload")
    public ResponseEntity<Void> upload(@RequestParam String id, @RequestParam MultipartFile video) {
        try {
            service.uploadVideo(id, video);
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
     * Delete saved poster which matches requested ids from S3 cloud storage and local db.
     * <p>
     * Also supports single id instance.
     * </p>
     *
     * @param videoIds ids split by ',' separator.
     * @return Response
     * <ul>
     *      <li>204 (NO_CONTENT)</li>
     *      <li>500 (INTERNAL_SERVER_ERROR)</li>
     * </ul>
     */
    @DeleteMapping("{videoIds}")
    public ResponseEntity<Void> delete(@PathVariable String videoIds) {
        try {
            service.deleteByIds(videoIds);
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

    // TODO create general answer when request param/body are illegal
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Required request body is missing or invalid.");
//    }

}