package ru.storage.person.userpic;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.person.Position;
import ru.storage.utils.S3GeneralOperations;
import software.amazon.awssdk.services.s3.model.S3Exception;

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
    String s3Key = PICTURES_STORAGE_FOLDER + "/" + userPic.getPosition().stringValue;
    S3GeneralOperations.uploadImage(s3Key, userPic.getId(), image);
    return savedMetadata;
  }

  /**
   * Retrieve poster images from S3 storage based on specified metadata IDs .
   *
   * @param position the category of the user pic (e.g., USER)
   * @param ids required ids
   * @return resource containing single byte array with the delimiter in between the images.
   * @throws S3Exception when an error occurs during the retrieval process
   * @throws NoSuchElementException when no matches are found for the provided ids
   */
  public Resource getUserPics(Position position, List<String> ids) {
    return S3GeneralOperations.getBinariesByIds(
        PICTURES_STORAGE_FOLDER + "/" + position.stringValue, ids);
  }

  /**
   * Delete user pics from S3 storage based by their IDs.
   *
   * @param position the category of the user pic (e.g., USER)
   * @param ids required ids
   * @throws S3Exception when an error occurs during the deletion process from S3 storage
   */
  public void deleteById(Position position, List<String> ids) {
    userPicRepo.deleteAllById(ids);
    S3GeneralOperations.deleteItems(
        PICTURES_STORAGE_FOLDER + "/" + position.stringValue, ids);
  }

}
