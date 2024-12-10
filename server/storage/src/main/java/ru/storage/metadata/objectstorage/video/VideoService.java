package ru.storage.metadata.objectstorage.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;
    private static final String FOLDER = "videos/";
    private static final Logger LOG = LoggerFactory.getLogger(VideoService.class);

    private final S3Client s3Client;
    private final VideoRepo videoRepo;

    public VideoService(S3Client s3Client, VideoRepo videoRepo) {
        this.s3Client = s3Client;
        this.videoRepo = videoRepo;
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

    /**
     * Save video metadata to a database.
     *
     * @param video video object metadata
     * @throws IllegalArgumentException when content type if not an image
     */
    public Video saveMetadata(Video video) {
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
                    .key(FOLDER + id)
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

    public String getFileContent(String bucketName, String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            InputStream inputStream = s3Client.getObject(request);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (S3Exception | IOException e) {
            e.printStackTrace();
            return "Failed to retrieve file: " + e.getMessage();
        }
    }
}
