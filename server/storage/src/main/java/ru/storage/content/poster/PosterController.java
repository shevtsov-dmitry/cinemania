package ru.storage.content.poster;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.exceptions.ParseIdException;
import ru.storage.utils.EncodedHttpHeaders;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@RequestMapping("api/v0/posters")
public class PosterController {

  private final PosterService service;

  public PosterController(PosterService service) {
    this.service = service;
  }

  /**
   * Upload poster into S3 cloud storage.
   *
   * @param id poster metadata id from mongodb db
   * @param image multipart file of image type
   * @return Response
   *     <ul>
   *       <li>201 (CREATED)
   *       <li>400 (BAD_REQUEST) with the cause header "Message" when invalid args
   *       <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message" when image wasn't saved
   *           into S3 cloud storage/li>
   *     </ul>
   */
  @PostMapping("upload")
  public ResponseEntity<Void> uploadImage(
      @RequestParam String id, @RequestParam MultipartFile image) {
    try {
      service.uploadImage(id, image);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (S3Exception e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get images from S3 cloud storage by IDs.
   *
   * @apiNote
   * <p>This method supports both single and multiple content metadata IDs, separated by commas.
   *
   * @param contentMetadataIds string of ids separated by comma
   * @return Response:
   *     <ul>
   *       <li>200 (OK) resouce containing single byte array with the delimiter in between the
   *           binary items
   *       <li>400 (BAD_REQUEST) with the cause header "Message" when content metadata IDs are
   *           invalid
   *       <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message" when an error occurs
   *    </ul>
   */
  @GetMapping(value = "{contentMetadataIds}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> getImagesByMetadataId(@PathVariable String contentMetadataIds) {
    try {
      return ResponseEntity.ok(service.getImagesMatchingMetadataIds(contentMetadataIds));
    } catch (ParseIdException e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (S3Exception e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Delete image from S3 cloud storage.
   *
   * @param id id
   * @returns Response
   *     <ul>
   *       <li>204 (NO_CONTENT) when success on delete
   *       <li>401 (BAD_REQUEST) with the cause header "Message" when param format is not like
   *           mongodb id standard
   *       <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message" when error performing
   *           operation in S3
   *     </ul>
   */
  @DeleteMapping("{id}")
  public ResponseEntity<Void> deleteImage(@PathVariable String id) {
    try {
      service.deleteByIds(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (ParseIdException e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (S3Exception e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
