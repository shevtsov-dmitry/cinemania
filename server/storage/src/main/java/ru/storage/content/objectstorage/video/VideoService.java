package ru.storage.content.objectstorage.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.content.ContentDetailsRepo;
import ru.storage.content.objectstorage.exceptions.ParseRequestIdException;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class VideoService {

    private final ContentDetailsRepo contentDetailsRepo;
    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;
    private static final String S3_FOLDER = "videos";
    private static final Logger LOG = LoggerFactory.getLogger(VideoService.class);

    private final S3Client s3Client;
    private final VideoRepo videoRepo;

    public VideoService(S3Client s3Client, VideoRepo videoRepo, ContentDetailsRepo contentDetailsRepo) {
        this.s3Client = s3Client;
        this.videoRepo = videoRepo;
        this.contentDetailsRepo = contentDetailsRepo;
    }

    /**
     * Assure a video processing by comparing input content type with expected.
     *
     * @param contentType contentType
     * @throws IllegalArgumentException when non image content used
     */
    private void assureVideoProcessing(String contentType) throws IllegalArgumentException {
        if (contentType == null || !contentType.startsWith("video")) {
            String errmes = "Ошибка при сохранении видео. Файл не является видеороликом. Был выбран файл типа " + contentType;
            LOG.warn(errmes);
            throw new IllegalArgumentException(errmes);
        }
    }


    // TODO create upload method by hls chunks

    /**
     * Save video metadata to a database.
     *
     * @param video video object metadata
     * @throws IllegalArgumentException when content type if not an image
     */
    public Video saveMetadata(Video video) {
        if (video == null) {
            LOG.warn("Error saving video object from request, because it is null.");
            throw new IllegalArgumentException("Метаданные видеофайла отсутствуют.");
        }
        assureVideoProcessing(video.getContentType());
        return videoRepo.save(video);
    }

    /**
     * @param id    id
     * @param video video multipart file from the form
     * @throws IllegalArgumentException when multipart file is not a video
     * @throws S3Exception              when error uploading file to S3
     */
    public void uploadVideo(String id, MultipartFile video) {
        assureVideoProcessing(video.getContentType());
        try (InputStream inputStream = video.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(S3_FOLDER + "/" + id)
                    .contentType(video.getContentType())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, video.getSize()));
        } catch (IOException | AwsServiceException e) {
            String errmes = "Ошибка при сохранении видео.";
            LOG.warn("{}. {}", errmes, e.getMessage());
            throw S3Exception.builder()
                    .message(errmes)
                    .build();
        }
    }

    /**
     * Find all matched images in S3 folder.
     *
     * @param idsSet required ids; using Set because ids cannot have duplicates
     * @return list of matched images names
     */
    private List<String> findMatchedS3Ids(Set<String> idsSet) {
        ListObjectsRequest lsRequest = ListObjectsRequest.builder()
                .bucket(bucketName)
                .prefix(S3_FOLDER)
                .build();

        return s3Client.listObjects(lsRequest).contents().stream()
                .skip(1)
                .map(S3Object::key)
                .filter(filepath -> {
                    String[] splitFilename = filepath.split("/");
                    String filename = splitFilename[splitFilename.length - 1];
                    return idsSet.contains(filename);
                })
                .toList();
    }

    /**
     * Deletes saved videos which matches requested ids from S3 and local db.
     *
     * @param videoIds ids split by ',' separator (can be single id)
     * @throws ParseRequestIdException when of invalid number format defined by api
     * @throws S3Exception             when image wasn't deleted
     */
    public void deleteByIds(String videoIds) {
        Set<String> idsSet;
        try {
            idsSet = Arrays.stream(videoIds.split(","))
                    .collect(Collectors.toSet());
        } catch (NumberFormatException e) {
            LOG.warn(e.getMessage());
            throw new ParseRequestIdException();
        }

        deleteFromLocalDb(idsSet);
        deleteFromS3(findMatchedS3Ids(idsSet));
    }

    private void deleteFromLocalDb(Set<String> idsSet) {
        idsSet.forEach(videoRepo::deleteById);
    }

    private void deleteFromS3(List<String> s3ImageIds) {
        s3ImageIds.forEach(id -> {
            var deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(id)
                    .build();
            try {
                s3Client.deleteObject(deleteObjectRequest);
            } catch (AwsServiceException e) {
                String errmes = "Ошибка при удалении видео из облачного хранилища S3 по их идентификаторам.";
                LOG.warn("{}. {}", errmes, e.getMessage());
                throw S3Exception.builder()
                        .message(errmes)
                        .build();
            }
        });
    }

}
