package ru.storage.userpic;

import java.util.NoSuchElementException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.exceptions.ParseIdException;
import ru.storage.utils.EncodedHttpHeaders;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@RequestMapping("api/v0/content-creators/user-pics")
public class UserPicsController {

  private final UserPicsService userPicsService;

  public UserPicsController(UserPicsService userPicsService) {
    this.userPicsService = userPicsService;
  }

  /**
   * Upload or update the user pic.
   *
   * @param picCategory the category of the user pic (e.g., USER)
   * @param image the multipart file containing the user pic
   * @return Response:
   *     <ul>
   *       <li>201 CREATED - User pic uploaded successfully with saved metadata.
   *     </ul>
   */
  @PostMapping("upload")
  public ResponseEntity<UserPic> uploadUserPic(
      @RequestParam PicCategory picCategory, @RequestParam MultipartFile image) {
    var userPic =
        new UserPic(null, image.getContentType(), image.getOriginalFilename(), picCategory);
    var savedUserPicDetails = userPicsService.saveImageMetadata(userPic);
    userPicsService.upload(savedUserPicDetails, image);
    return new ResponseEntity<>(savedUserPicDetails, HttpStatus.CREATED);
  }

  /**
   * Get images from S3 cloud storage by IDs.
   *
   * @apiNote
   *     <p>This method supports both single and multiple content metadata IDs, separated by commas.
   * @param PicCategory picCategory the category of the pic (e.g., ACTOR, USER)
   * @param ids string of ids separated by comma
   * @return Response:
   *     <ul>
   *       <li>200 (OK) resouce containing single byte array with the delimiter in between the
   *           binary items
   *       <li>400 (BAD_REQUEST) with the cause header "Message" when content metadata IDs are
   *           invalid
   *       <li>404 (NOT_FOUND) with the cause header "Message" when no matching items are found
   *       <li>500 (INTERNAL_SERVER_ERROR) with the cause header "Message" when an error occurs
   *     </ul>
   */
  @GetMapping(value = "{picCategory}/{ids}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> getUserPics(
      @PathVariable PicCategory picCategory, @PathVariable String ids) {
    try {
      return new ResponseEntity<>(userPicsService.getUserPics(picCategory, ids), HttpStatus.OK);
    } catch (NoSuchElementException e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.NOT_FOUND);
    } catch (ParseIdException e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (S3Exception e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Delete user pic.
   *
   * @param picCategory the category of the user pic (e.g., USER)
   * @param id the ID of the user pic
   * @return Response:
   *     <ul>
   *       <li>204 (No Content)
   *     </ul>
   */
  @DeleteMapping("{picCategory}/{ids}")
  public ResponseEntity<Void> deleteUserPic(
      @PathVariable PicCategory picCategory, @PathVariable String ids) {
    userPicsService.delete(picCategory, ids);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
