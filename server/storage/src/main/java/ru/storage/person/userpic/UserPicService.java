package ru.storage.person.userpic;

import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.person.PersonCategory;
import ru.storage.utils.S3GeneralOperations;

@Service
public class UserPicService {

  private final UserPicRepo userPicRepo;
  private static final String PICTURES_STORAGE_FOLDER = "userpic";

  public UserPicService(UserPicRepo userPicsRepo) {
    this.userPicRepo = userPicsRepo;
  }

  /**
   * Uploads a new image to the specified storage folder and associates it with the given user pic
   * entity.
   *
   * @param userPic the entity to associate with the uploaded image
   * @param image the multipart file to upload
   * @return the created user pic metadataa with the new image details
   * @throws IllegalArgumentException when the multipart file is not an image file.
   * @throws S3Exception when an error occurs during the upload process.
   */
  public UserPic uploadImage(UserPic userPic, MultipartFile image) {
    UserPic savedMetadata = userPicRepo.save(userPic);
    String s3Key = PICTURES_STORAGE_FOLDER + "/" + userPic.getPersonCategory().stringValue;
    S3GeneralOperations.uploadImage(s3Key, userPic.getId(), image);
    return savedMetadata;
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
    userPicRepo.deleteAllById(parsedIds);
    S3GeneralOperations.deleteItems(
        PICTURES_STORAGE_FOLDER + "/" + personCategory.stringValue, parsedIds);
  }

  /**
   * Delete user pics from S3 storage based by their IDs.
   *
   * @param s3Folder the folder in S3 storage where the user pics are stored
   * @param ids a comma-separated string of content metadata IDs
   * @throws S3Exception when an error occurs during the deletion process from S3 storage
   */
  public void delete(String s3Folder, String ids) {
    List<String> parsedIds = Arrays.asList(ids.split(",")).stream().map(String::trim).toList();
    userPicRepo.deleteAllById(parsedIds);
    S3GeneralOperations.deleteItems(s3Folder, parsedIds);
  }
}
