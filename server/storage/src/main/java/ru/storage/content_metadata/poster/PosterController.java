package ru.storage.content_metadata.poster;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.exceptions.ParseIdException;
import ru.storage.utils.EncodedHttpHeaders;
import ru.storage.utils.ProjectStandardUtils;
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
   * @param id saved metadata id from db
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
   * Get map list with poster images and their corresponding ids from S3 cloud storage by IDs.
   *
   * @param ids string of ids separated by comma
   * @return Response:
   *     <ul>
   *       <li>200 (OK) maps list with id and corresponding base 64 image
   *       <li>400 (BAD_REQUEST) with the cause header "Message" when content metadata IDs are
   *           invalid
   *       <li>404 (NOT_FOUND) with the cause header "Message" when no matching items are found
   *       <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message" when an error occurs
   *     </ul>
   *
   * @apiNote
   *     <p>This method supports both single and multiple content metadata IDs, separated by commas.
   */
  @GetMapping(value = "{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Map<String, Serializable>>> getImagesByMetadataIds(
      @PathVariable String ids) {

    try {
      return ResponseEntity.ok(
          service.getImagesMatchingMetadataIds(ProjectStandardUtils.parseIdsFromString(ids)));
    } catch (ParseIdException e) {
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

  /**
   * Delete image from S3 cloud storage.
   *
   * @param id id
   * @return Response
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
      service.deleteByIds(ProjectStandardUtils.parseIdsFromString(id));
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
