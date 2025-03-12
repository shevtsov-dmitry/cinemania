package ru.storage.utils;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

/** A utility class for general predefined operations with S3 storage. */
@Component
public class S3GeneralOperations {

  private final S3Client initS3Client;

  @Value("${custom.s3.BUCKET_NAME}")
  private String initBucketName;

  private static String bucketName;
  private static S3Client s3Client;

  private static final Logger LOG = LoggerFactory.getLogger(S3GeneralOperations.class);

  private S3GeneralOperations(S3Client initS3Client) {
    this.initS3Client = initS3Client;
  }

  @PostConstruct
  private void init() {
    bucketName = initBucketName;
    s3Client = initS3Client;
  }

  /**
   * Uploads an image to the specified S3 folder.
   *
   * <p>Stored image has the same name as the id.
   *
   * @param s3Folder the folder in S3 where the image will be stored.
   * @param id the unique identifier for the image.
   * @param image the image to be uploaded.
   * @throws IllegalArgumentException when the image is not an image file.
   * @throws S3Exception when an error occurs during the upload process.
   */
  public static void uploadImage(String s3Folder, String id, MultipartFile image) {
    BinaryContentUtils.assureImageProcessing(image.getContentType());
    try (InputStream inputStream = image.getInputStream()) {
      var putObjectRequest =
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(s3Folder + "/" + id)
              .contentType(image.getContentType())
              .build();
      InputStream compressedImage = BinaryContentUtils.compressImage(inputStream);
      s3Client.putObject(
          putObjectRequest,
          RequestBody.fromInputStream(compressedImage, compressedImage.available()));
      compressedImage.close();
    } catch (IOException | AwsServiceException e) {
      String errmes = "Ошибка при сохранении фотографии профиля.";
      LOG.warn("{}. {}", errmes, e.getMessage());
      throw S3Exception.builder().message(errmes).build();
    }
  }

  /**
   * Find all matched items in S3 folder.
   *
   * @param folder folder name which will be searched
   * @param ids required ids
   * @return list of matched item names by id
   * @throws NoSuchElementException when no matching items are found
   */
  public static List<String> findMatchedIds(String folder, Collection<String> ids) {
    var lsRequest = ListObjectsRequest.builder().bucket(bucketName).prefix(folder).build();

    List<String> matchedIds =
        s3Client.listObjects(lsRequest).contents().stream()
            .map(S3Object::key)
            .filter(
                filepath -> {
                  String[] splitFilename = filepath.split("/");
                  String filename = splitFilename[splitFilename.length - 1];
                  return ids.contains(filename);
                })
            .toList();

    if (matchedIds.isEmpty()) {
      throw new NoSuchElementException(
          "Не удалось найти запрощенные изображения по ID: %s".formatted(ids));
    } else {
      return matchedIds;
    }
  }

  /**
   * Retrieve items by id from S3 by ids.
   *
   * @param s3Folder folder name which will be searched
   * @param ids required ids
   * @return resource containing single byte array with the delimiter in between the binary items.
   * @throws S3Exception when an error occurs during the retrieval process
   * @throws NoSuchElementException when no matches are found for the provided ids
   * @apiNote
   *     <p>This method supports both single and multiple content metadata IDs, separated by commas.
   */
  public static Resource getBinariesByIds(String s3Folder, List<String> ids) {
    List<byte[]> binaryItemsList =
        S3GeneralOperations.findMatchedIds(s3Folder, ids).stream()
            .map(S3GeneralOperations::findItemByKey)
            .toList();
    return new ByteArrayResource(BinaryContentUtils.combineWithDefaultDelimiter(binaryItemsList));
  }

  /**
   * Retrieve items by id from S3 by ids.
   *
   * @param s3Folder folder name which will be searched
   * @param ids required ids
   * @return maps list with id and corresponding base 64 image
   * @throws S3Exception when an error occurs during the retrieval process
   * @throws NoSuchElementException when no matches are found for the provided ids
   * @apiNote
   *     <p>This method supports both single and multiple content metadata IDs, separated by commas.
   */
  public static List<Map<String, Serializable>> getMapsByIds(String s3Folder, List<String> ids) {
    return S3GeneralOperations.findMatchedIds(s3Folder, ids).stream()
        .map(id -> Map.of("id", id, "image", findItemByKey(id)))
        .toList();
  }

  /**
   * Retrieve items by id from S3 by ids.
   *
   * @param s3Folder folder name which will be searched
   * @param ids required ids
   * @return resource containing single byte array with the delimiter in between the binary items.
   * @throws S3Exception when an error occurs during the retrieval process
   * @throws NoSuchElementException when no matches are found for the provided ids
   */
  public static Resource getBinariesByIds(String s3Folder, Collection<String> ids) {
    List<byte[]> binaryItemsList =
        S3GeneralOperations.findMatchedIds(s3Folder, ids).stream()
            .map(S3GeneralOperations::findItemByKey)
            .toList();
    return new ByteArrayResource(BinaryContentUtils.combineWithDefaultDelimiter(binaryItemsList));
  }

  /**
   * Find specific binary item from S3 by key(s3 file path).
   *
   * @param s3FilePath required key
   * @return byte array item content
   * @throws S3Exception when an error occurs during the retrieval process
   */
  private static byte[] findItemByKey(String s3FilePath) {
    var getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(s3FilePath).build();
    try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
      return s3Object.readAllBytes();
    } catch (Exception e) {
      String errmes = "Ошибка при получении изображений из облачного хранилища";
      LOG.warn("{}. {}", errmes, e.getMessage());
      throw S3Exception.builder().message(errmes).build();
    }
  }

  /**
   * Delete saved items which matches requested ids from S3.
   *
   * @param s3Folder the folder name in S3 where images are stored
   * @param ids a comma-separated string of content metadata IDs
   * @throws S3Exception when items wasn't deleted
   */
  public static void deleteItems(String s3Folder, List<String> ids) {
    ids.stream().map(id -> s3Folder + "/" + id).forEach(S3GeneralOperations::deleteItemByPath);
  }

  /**
   * Delete saved items which matches requested ids from S3.
   *
   * @param s3Folder the folder name in S3 where images are stored
   * @param ids ids to delete
   * @throws S3Exception when items wasn't deleted
   */
  public static void deleteItems(String s3Folder, Collection<String> ids) {
    ids.stream().map(id -> s3Folder + "/" + id).forEach(S3GeneralOperations::deleteItemByPath);
  }

  /**
   * Delete saved items which matches requested ids from S3.
   *
   * @param path the full path to the item in S3
   * @throws S3Exception when items wasn't deleted
   */
  private static void deleteItemByPath(String path) {
    try {
      var deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(path).build();
      s3Client.deleteObject(deleteObjectRequest);
    } catch (AwsServiceException e) {
      String errmes =
          "Ошибка при удалении элемента из облачного хранилища S3 по их идентификаторам."
              + "Была произведена попытка удалить "
              + path;
      LOG.warn("{}. {}", errmes, e.getMessage());
      throw S3Exception.builder().message(errmes).build();
    }
  }
}
