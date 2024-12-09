package ru.storage.metadata.objectstorage.poster;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnailator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.metadata.ContentMetadataRepo;
import ru.storage.metadata.objectstorage.exceptions.NoMetadataRelationException;
import ru.storage.metadata.objectstorage.exceptions.ParseRequestIdException;
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

    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;
    private static final String FOLDER = "posters/";
    private static final Logger LOG = LoggerFactory.getLogger(PosterService.class);

    private final PosterRepo posterRepo;
    private final ContentMetadataRepo contentMetadataRepo;
    private final S3Client s3Client;

    public PosterService(PosterRepo posterRepo, ContentMetadataRepo contentMetadataRepo, S3Client s3Client) {
        this.posterRepo = posterRepo;
        this.contentMetadataRepo = contentMetadataRepo;
        this.s3Client = s3Client;
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
     * @param poster poster object with metadata
     * @throws NoMetadataRelationException when poster doesn't have field {@code content} field
     * @throws IllegalArgumentException    when content type if not an image
     */
    public Poster saveMetadata(Poster poster) {
        assureImageProcessing(poster.getContentType());
        try {
            return posterRepo.save(poster);
        } catch (NoMetadataRelationException e) {
            LOG.warn(NoMetadataRelationException.ERROR_MESSAGE);
            throw new NoMetadataRelationException();
        }
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
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(FOLDER + id)
                    .contentType(image.getContentType())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(compressImage(inputStream), image.getSize()));
        } catch (IOException | AwsServiceException e) {
            String errmes = "Ошибка при сохранении постера для видео.";
            LOG.warn("{}. {}", errmes, e.getMessage());
            throw S3Exception.builder()
                    .message(errmes)
                    .build();
        }
    }

    /**
     * Retrieve poster images from S3 storage based on specified metadata IDs.
     *
     * <p>This method supports both single and multiple content metadata IDs, separated by commas.
     * For example, {@code "4,2,592,101,10"}.</p>
     *
     * @param contentMetadataIds a comma-separated string of content metadata IDs
     * @return List of matched images from S3.
     * @throws ParseRequestIdException when invalid number format defined by api
     * @throws UncheckedIOException    when image retrieval from S3
     */
    public List<byte[]> getImagesMatchingMetadataIds(String contentMetadataIds) {
        List<byte[]> images = new ArrayList<>();
        Set<Long> idsSet;
        try {
            idsSet = Arrays.stream(contentMetadataIds.split(","))
                    .mapToLong(Long::parseLong)
                    .boxed()
                    .collect(Collectors.toSet());
        } catch (NumberFormatException e) {
            LOG.warn(e.getMessage());
            throw new ParseRequestIdException();
        }

        List<Long> matchedS3Ids = lsPosterStorageFolder(idsSet);
        matchedS3Ids.forEach(id -> {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(id.toString())
                    .build();

            // Retrieve the object and add its contents as a byte array to the list
            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
                images.add(s3Object.readAllBytes());
            } catch (Exception e) {
                String errmes = "Ошибка при получении изображений постеров из облачного хранилища";
                LOG.warn(errmes);
                throw S3Exception.builder()
                        .message(errmes)
                        .build();
            }
        });

        return images;
    }

    /**
     * Util method for savePoster() which compresses the input image under poster standard.
     *
     * @param inputStream - initial image byte stream
     * @return compressed image
     */
    private InputStream compressImage(InputStream inputStream) {
        try {
            var outStream = new FastByteArrayOutputStream();
            Thumbnailator.createThumbnail(inputStream, outStream, 250, 370);
            return outStream.getInputStream();
        } catch (IOException e) {
            LOG.error("Ошибка при сжатии файла изображения. Вызвана: {}", e.getMessage());
            return inputStream;
        }
    }


    /**
     * List all matched images in S3 folder.
     *
     * @param idsSet required ids
     * @return List of matched images names
     */
    private List<Long> lsPosterStorageFolder(Set<Long> idsSet) {
        ListObjectsRequest lsRequest = ListObjectsRequest.builder()
                .bucket(bucketName)
                .prefix(FOLDER)
                .build();
        ListObjectsResponse lsResponse = s3Client.listObjects(lsRequest);

        return lsResponse.contents().stream()
                .mapToLong(s3Object -> Long.parseLong(s3Object.key().substring(FOLDER.length())))
                .filter(idsSet::contains)
                .boxed()
                .toList();
    }

    /**
     * Updates existing poster in S3 storage
     *
     * @param metadataId content metadata id
     * @param image      new multipart form data image
     * @throws ParseRequestIdException when invalid number format defined by api
     * @throws UncheckedIOException    when s3 couldn't save existing image for some reason
     */
//    @Transactional
//    public void updateExistingImage(Long metadataId, MultipartFile image) {
//        assureImageProcessing(image.getContentType());
//        ContentMetadata contentMetadata = contentMetadataRepo.findById(metadataId).orElseThrow(NoMetadataRelationException::new);
//
//        try {
//            posterRepo.updatePosterByContentMetadata(contentMetadata, image.getOriginalFilename(), image.getContentType());
//        } catch (Exception e) {
//            LOG.warn("Произошла ошибка при попытке изменить связь постера с одного  ");
//        }
//
//        final Long s3ImageId = lsPosterStorageFolder(Set.of(metadataId)).getFirst();
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(String.valueOf(s3ImageId))
//                .contentType(image.getContentType())
//                .build();
//        try {
//            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
//        } catch (IOException e) {
//            String errmes = "Не удалось обновить существующий постер в облачном хранилище.";
//            LOG.warn("{}. {}", errmes, e.getMessage());
//            throw S3Exception.builder()
//                    .message(errmes)
//                    .build();
//        }
//    }

    /**
     * Deletes saved poster which matches requested ids from S3 and local db.
     *
     * <p>
     * Also supports single id instance. example: {@code "4"}.
     * </p>
     *
     * @param contentMetadataIds ids split by ',' separator. For example: {@code "4,2,592,101,10"}
     * @throws ParseRequestIdException when of invalid number format defined by api
     * @throws S3Exception             when image wasn't deleted
     */
//    @Transactional
//    public void deleteByIds(String contentMetadataIds) {
//        Set<Long> idsSet;
//        try {
//            idsSet = Arrays.stream(contentMetadataIds.split(","))
//                    .mapToLong(Long::parseLong)
//                    .boxed()
//                    .collect(Collectors.toSet());
//        } catch (Exception e) {
//            LOG.warn(e.getMessage());
//            throw new ParseRequestIdException();
//        }
//
//        idsSet.forEach(posterRepo::deleteByContentMetadataId);
//
//        List<Long> s3ImageIds = lsPosterStorageFolder(idsSet);
//        s3ImageIds.forEach(id -> {
//            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(FOLDER + id.toString())
//                    .build();
//            try {
//                s3Client.deleteObject(deleteObjectRequest);
//            } catch (AwsServiceException e) {
//                String errmes = "Ошибка при удалении постеров по их идентификаторам.";
//                LOG.warn("{}. {}", errmes, e.getMessage());
//                throw S3Exception.builder()
//                        .message(errmes)
//                        .build();
//            }
//        });
//    }


}
