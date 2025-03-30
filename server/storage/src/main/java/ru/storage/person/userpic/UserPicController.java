package ru.storage.person.userpic;

import java.util.List;
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
import ru.storage.exceptions.ParseEnumException;
import ru.storage.exceptions.ParseIdException;
import ru.storage.person.Position;
import ru.storage.utils.EncodedHttpHeaders;
import ru.storage.utils.ProjectStandardUtils;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@RequestMapping("api/v0/metadata/content-creators/user-pics")
public class UserPicController {

  private final UserPicService userPicService;

  public UserPicController(UserPicService userPicsService) {
    this.userPicService = userPicsService;
  }

  /**
   * Upload or update the user pic.
   *
   * @param personCategory the category of the user pic (e.g., USER)
   * @param image the multipart file containing the user pic
   * @return Response:
   *     <ul>
   *       <li>201 created when user pic uploaded successfully with saved metadata.
   *       <li>400 bad request - invalid request parameters, missing file or non image file trying
   *           to be uploaded.
   *       <li>500 internal server error - unexpected errors during upload or update process.
   *     </ul>
   *     Headers:
   *     <ul>
   *       <li>Message: with error message if any.
   *     </ul>
   */
  @PostMapping("upload")
  public ResponseEntity<UserPic> uploadUserPic(
      @RequestParam String personCategory, @RequestParam MultipartFile image) {
    Position category;
    try {
      category = Position.valueOf(personCategory.toUpperCase());
    } catch (IllegalArgumentException e) {
      String errmes = (new ParseEnumException(Position.class)).getMessage();
      return new ResponseEntity<>(new EncodedHttpHeaders(errmes), HttpStatus.BAD_REQUEST);
    }

    try {
      UserPic userPicMetadata =
          UserPic.builder()
              .filename(image.getOriginalFilename())
              .contentType(image.getContentType())
              .size(image.getSize())
              .position(category)
              .build();
      UserPic savedUserPicMetadata = userPicService.uploadImage(userPicMetadata, image);
      return new ResponseEntity<>(savedUserPicMetadata, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(new EncodedHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (S3Exception e) {
      return new ResponseEntity<>(
          new EncodedHttpHeaders(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get images from S3 cloud storage by IDs.
   *
   * @apiNote
   *     <p>This method supports both single and multiple content metadata IDs, separated by commas.
   * @param personCategory the category of the pic (e.g., ACTOR, USER)
   * @param ids string of ids separated by comma
   * @return Response:
   *     <ul>
   *       <li>200 (ok) resouce containing single byte array with the delimiter in between the
   *           binary items
   *       <li>400 (bad request) with the cause header "Message" when content metadata IDs are
   *           invalid or person category is invalid
   *       <li>404 (not found) WITH THE CAUSE HEADER "Message" WHEN NO MATCHING ITEMS ARE FOUND
   *       <li>500 (internal server error) with the cause header "Message" when an error occurs
   *     </ul>
   */
  @GetMapping(value = "{personCategory}/{ids}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> getUserPics(
      @PathVariable String personCategory, @PathVariable String ids) {
    Position category;
    try {
      category = Position.valueOf(personCategory.toUpperCase());
    } catch (IllegalArgumentException e) {
      String errmes = (new ParseEnumException(Position.class)).getMessage();
      return new ResponseEntity<>(null, new EncodedHttpHeaders(errmes), HttpStatus.BAD_REQUEST);
    }

    try {
      List<String> parsedIds = ProjectStandardUtils.parseIdsFromString(ids);
      return new ResponseEntity<>(userPicService.getUserPics(category, parsedIds), HttpStatus.OK);
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
   * @param personCategory the category of the user pic (e.g., USER)
   * @param id the ID of the user pic
   * @return Response:
   *     <ul>
   *       <li>204 (no content)
   *       <li>400 (bad request) when invalid personCategory
   *     </ul>
   */
  @DeleteMapping("{personCategory}/{id}")
  public ResponseEntity<Void> deleteUserPic(
      @PathVariable String personCategory, @PathVariable String id) {
    Position category;
    try {
      category = Position.valueOf(personCategory.toUpperCase());
    } catch (IllegalArgumentException e) {
      String errmes = (new ParseEnumException(Position.class)).getMessage();
      return new ResponseEntity<>(null, new EncodedHttpHeaders(errmes), HttpStatus.BAD_REQUEST);
    }
    List<String> parsedIds = ProjectStandardUtils.parseIdsFromString(id);
    try {
      userPicService.deleteById(category, parsedIds);
    } catch (ParseIdException e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
