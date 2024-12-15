package ru.storage.content.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.content.common.S3GeneralOperations;
import ru.storage.content.exceptions.ParseRequestIdException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;


@Service
public class VideoService {

    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;
    private static final String S3_FOLDER = "videos";
    private static final Logger LOG = LoggerFactory.getLogger(VideoService.class);

    private final S3Client s3Client;
    private final VideoRepo videoRepo;
    private final S3GeneralOperations s3GeneralOperations;

    public VideoService(S3Client s3Client, VideoRepo videoRepo, S3GeneralOperations s3GeneralOperations) {
        this.s3Client = s3Client;
        this.videoRepo = videoRepo;
        this.s3GeneralOperations = s3GeneralOperations;
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
     * @param videoMetadata video object metadata
     * @throws IllegalArgumentException when content type if not an image
     */
    public VideoMetadata saveMetadata(VideoMetadata videoMetadata) {
        if (videoMetadata == null) {
            LOG.warn("Error saving video object from request, because it is null.");
            throw new IllegalArgumentException("Метаданные видеофайла отсутствуют.");
        }
        assureVideoProcessing(videoMetadata.getContentType());
        return videoRepo.save(videoMetadata);
    }

    /**
     * @param id    id
     * @param video video multipart file from the form
     * @throws IllegalArgumentException when multipart file is not a video
     * @throws S3Exception              when error uploading file to S3
     */
    public void uploadVideo(String id, MultipartFile video) {
        assureVideoProcessing(video.getContentType());
//        try (InputStream inputStream = video.getInputStream()) {
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(S3_FOLDER + "/" + id)
//                    .contentType(video.getContentType())
//                    .build();
//            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, video.getSize()));
//        } catch (IOException | AwsServiceException e) {
//            String errmes = "Ошибка при сохранении видео.";
//            LOG.warn("{}. {}", errmes, e.getMessage());
//            throw S3Exception.builder()
//                    .message(errmes)
//                    .build();
//        }
    }

    /**
     * Delete related content from local metadata db and also from S3 storage.
     *
     * @param unparsedIds a comma-separated string of content metadata IDs
     * @throws ParseRequestIdException when of invalid number format defined by api
     * @throws S3Exception             when image wasn't deleted
     */
    public void deleteByIds(String unparsedIds) {
        List<String> ids = Arrays.asList(unparsedIds.split(","));
        if (ids.isEmpty()) {
            throw new ParseRequestIdException();
        }
        ids.forEach(videoRepo::deleteById);
        s3GeneralOperations.deleteFromS3(S3_FOLDER, ids);
    }

}