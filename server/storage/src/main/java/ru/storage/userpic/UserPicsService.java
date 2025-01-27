package ru.storage.userpic;

import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.storage.person.PersonCategory;
import ru.storage.utils.S3GeneralOperations;

@Service
public class UserPicsService {

  private final UserPicsRepo userPicsRepo;
  private static final String PICTURES_STORAGE_FOLDER = "userpic";

  public UserPicsService(UserPicsRepo userPicsRepo) {
    this.userPicsRepo = userPicsRepo;
  }

  /**
   * Save user pic image metadata to the database.
   *
   * @param userPic the user pic metadata object to be saved
   * @return The saved user pic metadata object from mongoDB
   */
  public UserPic saveImageMetadata(UserPic userPic) {
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
    S3GeneralOperations.uploadImage(
        PICTURES_STORAGE_FOLDER + "/" + userPic.getPersonCategory().stringValue,
        userPic.getId(),
        image);
  }

  /**
   * Retrieve poster images from S3 storage based on specified metadata IDs .
   *
   * @param personCategory the category of the user pic (e.g., USER)
   * @param ids a comma-separated string of content metadata IDs
   * @return resouce containing single byte array with the delimiter in between the images.
   * @throws S3Exception when an error occurs during the retrieval process
   * @throws NoSuchElementException when no matches are found for the provided ids
   */
  public Resource getUserPics(PersonCategory personCategory, String ids) {
    return S3GeneralOperations.getItemsByIds(
        PICTURES_STORAGE_FOLDER + "/" + personCategory.stringValue, ids);
  }

  /**
   * Delete user pics from S3 storage based by their IDs.
   *
   * @param personCategory the category of the user pic (e.g., USER)
   * @param ids a comma-separated string of content metadata IDs
   * @throws S3Exception when an error occurs during the deletion process from S3 storage
   */
  public void delete(PersonCategory personCategory, String ids) {
    List<String> parsedIds = Arrays.asList(ids.split(",")).stream().map(String::trim).toList();
    userPicsRepo.deleteAllById(parsedIds);
    S3GeneralOperations.deleteItems(
        PICTURES_STORAGE_FOLDER + "/" + personCategory.stringValue, parsedIds);
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
