package ru.storage.content.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Collection;
import java.util.List;

@Component
public class S3GeneralOperations {

    private static final Logger log = LoggerFactory.getLogger(S3GeneralOperations.class);
    private final S3Client s3Client;
    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;

    public S3GeneralOperations(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Find all matched items in S3 folder.
     *
     * @param folder folder name which will be searched
     * @param ids    required ids
     * @return list of matched item names by id
     */
    public List<String> findMatchedIds(String folder, Collection<String> ids) {
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
     * Delete saved videos which matches requested ids from S3.
     *
     * @param ids ids to delete
     * @throws S3Exception when image wasn't deleted
     */
    public void deleteFromS3(String folderName, Collection<String> ids) {
        ids.forEach(id -> {
            var path = folderName + "/" + id;
            var deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(path)
                    .build();
            try {
                s3Client.deleteObject(deleteObjectRequest);
            } catch (AwsServiceException e) {
                String errmes = "Ошибка при удалении элемента из облачного хранилища S3 по их идентификаторам." +
                        "Была произведена попытка удалить " + id;
                log.warn("{}. {}", errmes, e.getMessage());
                throw S3Exception.builder()
                        .message(errmes)
                        .build();
            }
        });
    }

}
