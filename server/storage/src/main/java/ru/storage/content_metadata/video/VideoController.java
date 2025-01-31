package ru.storage.content_metadata.video;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.storage.exceptions.ParseIdException;
import ru.storage.utils.EncodedHttpHeaders;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@RequestMapping("api/v0/videos")
public class VideoController {
    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

    /**
     * Upload video into S3 cloud storage by chunks
     *
     * @param id    video metadata id from mongodb
     * @param video multipart file of image type
     * @return Response
     * <ul>
     *     <li>201 (CREATED)</li>
     *     <li>400 (BAD_REQUEST) when invalid args</li>
     *     <li>500 (INTERNAL_SERVER_ERROR) when video wasn't saved into S3 cloud storage</li>
     * </ul>
     * Headers
     * <ul>
     *      <li>custom header "Message" encoded URL string which describes error message</li>
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
     * Delete related content from local metadata db and also from S3 storage.
     *
     * @param ids is a comma-separated string of content metadata IDs
     * @return Response
     * <ul>
     *     <li>201 (CREATED)</li>
     *     <li>400 (BAD_REQUEST) when error parsing ids</li>
     *     <li>500 (INTERNAL_SERVER_ERROR) when video wasn't saved into S3 cloud storage</li>
     * </ul>
     * Headers
     * <ul>
     *      <li>custom header "Message" encoded URL string which describes error message</li>
     * </ul>
     */
    @DeleteMapping("{ids}")
    public ResponseEntity<Void> delete(@PathVariable String ids) {
        try {
            service.deleteByIds(ids);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ParseIdException e) {
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
