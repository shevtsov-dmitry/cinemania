package ru.storage.content.poster;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import ru.storage.content.common.BinaryContentUtils;
import ru.storage.content.common.S3GeneralOperations;
import ru.storage.content.exceptions.ParseRequestIdException;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class PosterService {

    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;
    private static final String S3_FOLDER = "posters";
    private static final Logger LOG = LoggerFactory.getLogger(PosterService.class);
    private final PosterRepo posterRepo;
    private final S3Client s3Client;

    public PosterService(PosterRepo posterRepo, S3Client s3Client) {
        this.posterRepo = posterRepo;
        this.s3Client = s3Client;
    }

    @PostConstruct
    public void init() {
        Assert.notNull(bucketName, "переменная BUCKET_NAME должна быть указана в конфигурации application.properties.");
    }

    /**
     * Save poster metadata in database
     *
     * @param posterMetadata poster object with metadata
     * @throws IllegalArgumentException when content type if not an image
     */
    public PosterMetadata saveMetadata(PosterMetadata posterMetadata) {
        if (posterMetadata == null) {
            LOG.warn("Error saving poster object from request, because it is null.");
            throw new IllegalArgumentException("Метаданные постера отсутствуют.");
        }
        BinaryContentUtils.assureImageProcessing(posterMetadata.getContentType());
        return posterRepo.save(posterMetadata);
    }

    /**
     * Upload image to S3 cloud storage.
     *
     * @param id    poster metadata id from mongodb db
     * @param image form data image
     * @throws IllegalArgumentException when multipart file is not an image
     * @throws S3Exception              when image wasn't saved to S3 cloud storage
     */
    public void uploadImage(String id, MultipartFile image) {
        BinaryContentUtils.assureImageProcessing(image.getContentType());
        try (InputStream inputStream = image.getInputStream()) {
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(S3_FOLDER + "/" + id)
                    .contentType(image.getContentType())
                    .build();
            InputStream compressedImage = BinaryContentUtils.compressImage(inputStream);
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(compressedImage, compressedImage.available()));
            compressedImage.close();
        } catch (IOException | AwsServiceException e) {
            String errmes = "Ошибка при сохранении постера для видео.";
            LOG.warn("{}. {}", errmes, e.getMessage());
            throw S3Exception.builder()
                    .message(errmes)
                    .build();
        }
    }

    /**
     * Retrieve poster images from S3 storage based on specified metadata IDs .
     *
     * @param unparsedIds a comma-separated string of content metadata IDs
     * @return List of matched images from S3.
     * @throws S3Exception when an error occurs during the retrieval process.
     */
    public List<Pair<String, byte[]>> getImagesMatchingMetadataIds(String unparsedIds) {
        return S3GeneralOperations.getItemsByIds(S3_FOLDER, unparsedIds);
    }

    /**
     * Delete related content instances from local metadata db and also from S3 storage.
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
        ids.forEach(posterRepo::deleteById);
        S3GeneralOperations.deleteItems(S3_FOLDER, ids);
    }
    
}
