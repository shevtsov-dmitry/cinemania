package ru.storage.content.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
public class S3GeneralOperations {

    private final S3Client initS3Client;
    @Value("${custom.s3.BUCKET_NAME}")
    private String initBucketName;
    
    private static String bucketName;
    private static S3Client s3Client;

    private static final Logger log = LoggerFactory.getLogger(S3GeneralOperations.class);
    
    public S3GeneralOperations(S3Client s3Client) {
        this.initS3Client = s3Client;
    }

    @PostConstruct
    private void init() {
        bucketName = initBucketName;
        s3Client = initS3Client;
    }

    /**
     * Find all matched items in S3 folder.
     *
     * @param folder folder name which will be searched
     * @param ids    required ids
     * @return list of matched item names by id
     */
    public static List<String> findMatchedIds(String folder, Collection<String> ids) {
        var lsRequest = ListObjectsRequest.builder()
                .bucket(bucketName)
                .prefix(folder)
                .build();

        return s3Client.listObjects(lsRequest).contents().stream()
                .skip(1)
                .map(S3Object::key)
                .filter(filepath -> {
                    String[] splitFilename = filepath.split("/");
                    String filename = splitFilename[splitFilename.length - 1];
                    return ids.contains(filename);
                })
                .toList();
    }

    /**
     * Retrieve poster images from S3 storage based on specified metadata IDs .
     *
     * @param s3Folder the folder name in S3 where images are stored
     * @param ids      a comma-separated string of content metadata IDs
     * @return List of matched ids and images from S3 in pair.
     * @throws S3Exception when an error occurs during the retrieval process
     */
    public static List<Pair<String, byte[]>> getItemsByIds(String s3Folder, String ids) {
        Set<String> idsSet = parseIds(ids);
        return S3GeneralOperations.findMatchedIds(s3Folder, idsSet).stream()
                .map(S3GeneralOperations::findItemByKey)
                .toList();
    }

    /**
     * Retrieve poster images from S3 storage based on specified metadata IDs .
     *
     * @param s3Folder the folder name in S3 where images are stored
     * @param ids      a collection of content metadata IDs
     *
     * @return List of matched ids and images from S3 in pair.
     * @throws S3Exception when an error occurs during the retrieval process
     */
    public static List<Pair<String, byte[]>> getItemsByIds(String s3Folder, Collection<String> ids) {
        return S3GeneralOperations.findMatchedIds(s3Folder, ids).stream()
                .map(S3GeneralOperations::findItemByKey)
                .toList();
    }

    /**
     * Retrieve poster images from S3 storage based on specified metadata IDs .
     *
     * @param key the key of the image in S3 bucket to retrieve
     * @return List of ids and matched images from S3 in pair.
     * @throws S3Exception when an error occurs during the retrieval process
     */
    private static Pair<String, byte[]> findItemByKey(String key) {
        var getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
            return Pair.of(key, s3Object.readAllBytes());
        } catch (Exception e) {
            String errmes = "Ошибка при получении изображений из облачного хранилища";
            log.warn("{}. {}", errmes, e.getMessage());
            throw S3Exception.builder()
                    .message(errmes)
                    .build();
        }
    }

    /**
     * Parse request ids into a set of unique strings.
     * 
     * @param ids ids to parse
     * @return set of unique strings
     */
    private static Set<String> parseIds(String ids) {
        return Arrays.asList(ids.split(",")).stream()
                .map(String::trim)
                .distinct()
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Delete saved items which matches requested ids from S3.
     *
     * @param s3Folder the folder name in S3 where images are stored
     * @param ids      a comma-separated string of content metadata IDs
     * @throws S3Exception when items wasn't deleted
     */
    public static void deleteItems(String s3Folder, String ids) {
        parseIds(ids).stream()
                .map(id -> s3Folder + "/" + id)
                .forEach(S3GeneralOperations::deleteItemByPath);
    }

    /**
     * Delete saved items which matches requested ids from S3.
     *
     * @param s3Folder the folder name in S3 where images are stored
     * @param ids      ids to delete
     * @throws S3Exception when items wasn't deleted
     */
    public static void deleteItems(String s3Folder, Collection<String> ids) {
        ids.stream()
                .map(id -> s3Folder + "/" + id)
                .forEach(S3GeneralOperations::deleteItemByPath);
    }

    /**
     * Delete saved items which matches requested ids from S3.
     *
     * @param path the full path to the item in S3
     * @throws S3Exception when items wasn't deleted
     */
    private static void deleteItemByPath(String path) {
        try {
            var deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(path)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (AwsServiceException e) {
            String errmes = "Ошибка при удалении элемента из облачного хранилища S3 по их идентификаторам." +
                    "Была произведена попытка удалить " + path;
            log.warn("{}. {}", errmes, e.getMessage());
            throw S3Exception.builder()
                    .message(errmes)
                    .build();
        }
    }

}
