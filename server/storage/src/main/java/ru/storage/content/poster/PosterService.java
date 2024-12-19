package ru.storage.content.poster;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnailator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.content.common.S3GeneralOperations;
import ru.storage.content.exceptions.ParseRequestIdException;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PosterService {

    private final S3GeneralOperations s3GeneralOperations;
    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;
    private static final String S3_FOLDER = "posters";
    private static final Logger LOG = LoggerFactory.getLogger(PosterService.class);
    private final PosterRepo posterRepo;
    private final S3Client s3Client;

    public PosterService(PosterRepo posterRepo, S3Client s3Client, S3GeneralOperations s3GeneralOperations) {
        this.posterRepo = posterRepo;
        this.s3Client = s3Client;
        this.s3GeneralOperations = s3GeneralOperations;
    }

    /**
     * Assure an image processing by comparing input content type with expected.
     *
     * @param contentType contentType
     * @throws IllegalArgumentException when non image content used
     */
    private void assureImageProcessing(String contentType) throws IllegalArgumentException {
        if (contentType == null || !contentType.startsWith("image")) {
            String errmes = "Ошибка при сохранении постера. Файл не является изображением. Был выбран файл типа " + contentType;
            LOG.warn(errmes);
            throw new IllegalArgumentException(errmes);
        }
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
        assureImageProcessing(posterMetadata.getContentType());
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
        assureImageProcessing(image.getContentType());
        try (InputStream inputStream = image.getInputStream()) {
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(S3_FOLDER + "/" + id)
                    .contentType(image.getContentType())
                    .build();
            InputStream compressedImage = compressImage(inputStream);
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
     * @throws ParseRequestIdException when invalid number format defined by api
     * @throws UncheckedIOException    when image retrieval from S3
     */
    public List<byte[]> getImagesMatchingMetadataIds(String unparsedIds) {
        List<byte[]> images = new ArrayList<>();
        Set<String> idsSet;
        try {
            idsSet = Arrays.stream(unparsedIds.split(","))
                    .collect(Collectors.toSet());
        } catch (NumberFormatException e) {
            LOG.warn(e.getMessage());
            throw new ParseRequestIdException();
        }

        List<String> matchedS3Ids = s3GeneralOperations.findMatchedIds(S3_FOLDER, idsSet);
        matchedS3Ids.forEach(key -> {
            var getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // Retrieve the object and add its contents as a byte array to the list
            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
                images.add(s3Object.readAllBytes());
            } catch (Exception e) {
                String errmes = "Ошибка при получении изображений постеров из облачного хранилища";
                LOG.warn("{}. {}", errmes, e.getMessage());
                throw S3Exception.builder()
                        .message(errmes)
                        .build();
            }
        });

        return images;
    }

    /**
     * Util method which compresses the input image on poster standard.
     *
     * @param inputStream initial image byte stream
     * @return compressed image
     */
    private InputStream compressImage(InputStream inputStream) {
        try {
            var outStream = new FastByteArrayOutputStream();
            Thumbnailator.createThumbnail(inputStream, outStream, 500, 370);
            return outStream.getInputStream();
        } catch (IOException e) {
            LOG.error("Ошибка при сжатии файла изображения. Вызвана: {}", e.getMessage());
            return inputStream;
        }
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
        s3GeneralOperations.deleteFromS3(S3_FOLDER, ids);
    }


}
