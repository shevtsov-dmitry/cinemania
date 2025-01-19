package ru.storage.userpic;

import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.utils.S3GeneralOperations;

@Service
public class UserPicsService {

  private final UserPicsRepo userPicsRepo;

  public UserPicsService(UserPicsRepo userPicsRepo) {
    this.userPicsRepo = userPicsRepo;
  }

  /**
   * Save user metadata to the database.
   *
   * @param userPic the user metadata object to be saved
   * @return The saved user metadata object from mongoDB
   */
  public UserPic saveMetadata(UserPic userPic) {
    return userPicsRepo.save(userPic);
  }

  /**
   * Upload image to S3 storage based on user pic metadata.
   *
   * @param userPic the user metadata object containing image details
   * @param image the multipart file representing the uploaded
   * @throws IllegalArgumentException if the image is not an image file.
   * @throws S3Exception if an error occurs during the upload process.
   */
  public void upload(UserPic userPic, MultipartFile image) {
    S3GeneralOperations.uploadImage(userPic.getPicCategory().stringValue, userPic.getId(), image);
  }

  /**
   * Retrieve poster images from S3 storage based on specified metadata IDs .
   *
   * @param picCategory the category of the user pic (e.g., USER)
   * @param ids a comma-separated string of content metadata IDs
   * @return resouce containing single byte array with the
   *     delimiter in between the images.
   * @throws S3Exception when an error occurs during the retrieval process
   */
  public Resource getInPairs(PicCategory picCategory, String ids) {
    return S3GeneralOperations.getItemsByIds(picCategory.stringValue, ids);
  }

  /**
   * Retrieve poster images from S3 storage based on specified metadata IDs .
   *
   * @param picCategory the category of the user pic (e.g., USER)
   * @param ids a comma-separated string of content metadata IDs
   * @return resouce containing single byte array with the
   *     delimiter in between the images.
   * @throws S3Exception when an error occurs during the retrieval process
   */
  public Resource getInPairs(String s3Folder, String ids) {
    return S3GeneralOperations.getItemsByIds(s3Folder, ids);
  }

  /**
   * Delete user pics from S3 storage based by their IDs.
   *
   * @param picCategory the category of the user pic (e.g., USER)
   * @param ids a comma-separated string of content metadata IDs
   * @throws S3Exception when an error occurs during the deletion process from S3 storage
   */
  public void delete(PicCategory picCategory, String ids) {
    List<String> parsedIds = Arrays.asList(ids.split(",")).stream().map(String::trim).toList();
    userPicsRepo.deleteAllById(parsedIds);
    S3GeneralOperations.deleteItems(picCategory.stringValue, parsedIds);
  }

  /**
   * Delete user pics from S3 storage based by their IDs.
   *
   * @param picCategory the category of the user pic (e.g., USER)
   * @param ids a comma-separated string of content metadata IDs
   * @throws S3Exception when an error occurs during the deletion process from S3 storage
   */
  public void delete(String s3Folder, String ids) {
    List<String> parsedIds = Arrays.asList(ids.split(",")).stream().map(String::trim).toList();
    userPicsRepo.deleteAllById(parsedIds);
    S3GeneralOperations.deleteItems(s3Folder, parsedIds);
  }
}
