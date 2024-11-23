package ru.storage.metadata.objectstorage.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.metadata.objectstorage.exceptions.NoMetadataRelationException;
import ru.storage.utility.EncodedHttpHeaders;

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
     *     <li>400 (BAD_REQUEST) with the cause header "Message"
     *          <ol>
     *              <li>when invalid arguments</li>
     *              <li>when try to save without content metadata relation</li>
     *          </ol>
     *     </li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<Video> saveMetadata(@RequestBody Video video) {
        try {
            final Video savedPosterMetadata = service.saveMetadata(video);
            return new ResponseEntity<>(savedPosterMetadata, HttpStatus.CREATED);
        } catch (NoMetadataRelationException | IllegalArgumentException e) {
            return new ResponseEntity<>(null,
                    new EncodedHttpHeaders(e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Upload video into S3 cloud storage by chunks
     *
     * @param videoId id of poster metadata from db
     * @param video   multipart file of image type
     * @return Response
     * <ul>
     *     <li>204 (NO_CONTENT)</li>
     *     <li>400 (BAD_REQUEST) when invalid args</li>
     *     <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message" when video wasn't saved into S3 cloud storage</li>
     * </ul>
     */
    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestBody MultipartFile video) {
//        try {
//            final String videoId = service.uploadVideo(file);
//            return new ResponseEntity<>(videoId, HttpStatus.OK);
//        } catch (NullPointerException e) {
//            return new ResponseEntity<>(e.getMessage(),
//                    new EncodedHttpHeaders(e.getMessage()),
//                    HttpStatus.BAD_REQUEST);
//        }
        return null;
    }

    // TODO create general answer when request param/body are illegal
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Required request body is missing or invalid.");
//    }

}
