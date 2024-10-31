package ru.storage.objectstorage.poster;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import net.coobird.thumbnailator.Thumbnailator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.metadata.ContentMetadata;
import ru.storage.metadata.MetadataRepo;
import ru.storage.objectstorage.poster.exceptions.CustomNumberFormatException;
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
    private static final Logger log = LoggerFactory.getLogger(PosterService.class);

    private final PosterRepo posterRepo;
    private final MetadataRepo metadataRepo;
    private final S3Client s3Client;

    public PosterService(PosterRepo posterRepo, MetadataRepo metadataRepo, S3Client s3Client) {
        this.posterRepo = posterRepo;
        this.metadataRepo = metadataRepo;
        this.s3Client = s3Client;
    }

    @PostConstruct
    public void init() {
        Assert.notNull(bucketName, "переменная BUCKET_NAME должна быть указана в конфигурации application.properties.");
    }

    /**
     * Saves poster metadata in database and image into S3 cloud storage
     *
     * @param metadataId - id of saved content metadata
     * @param file       - form data image file
     * @return saved poster object from db
     * @throws InvalidDataAccessApiUsageException - in case if method used without saved metadata instance, which retrieved by metadataRepo
     * @throws UncheckedIOException               - in case if image wasn't saved in S3 for some reason
     */
    public Poster savePoster(Long metadataId, MultipartFile file) throws InvalidDataAccessApiUsageException, UncheckedIOException {
        final Poster savedPoster;
        final String contentType = Optional.ofNullable(file.getContentType()).orElse("image/jpeg");
        try {
            ContentMetadata contentMetadata = metadataRepo.findById(metadataId).orElseThrow(NoSuchElementException::new);
            Poster newPosterInstance = new Poster(file.getName(), contentType, contentMetadata);
            savedPoster = posterRepo.save(newPosterInstance);
        } catch (NoSuchElementException e) {
            String errMessage = "Метод сохранения плаката не предназначен для работы без ссылки на таблицу метаданных.";
            log.error(errMessage);
            throw new InvalidDataAccessApiUsageException(errMessage);
        }
        try (InputStream inputStream = file.getInputStream()) {
            final String key = FOLDER + metadataId;
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(compressImage(inputStream), file.getSize()));
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить изображение плаката в S3 из-за {}", e);
        }
        return savedPoster;
    }

    /**
     * Util method for savePoster() which compresses the input image under poster standard
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
            log.error("Ошибка при сжатии файла изображения. Вызвана: {}", e.getMessage());
            return inputStream;
        }
    }

    /**
     * Retrieves poster images from S3 storage based on specified metadata IDs.
     *
     * <p>This method supports both single and multiple content metadata IDs, separated by commas.
     * For example, {@code "4,2,592,101,10"}.</p>
     *
     * @param contentMetadataIds a comma-separated string of content metadata IDs
     * @return List of matched images from S3.
     * @throws CustomNumberFormatException - in case of invalid number format defined by api
     * @throws UncheckedIOException        - in case of image retrieval from S3
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
            throw new CustomNumberFormatException();
        }

        idsSet.forEach(posterRepo::deleteByContentMetadataId);

        List<Long> matchedS3Ids = lsPosterStorageFolder(idsSet);
        matchedS3Ids.forEach(id -> {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(id.toString())
                    .build();

            // Retrieve the object and add its contents as a byte array to the list
            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
                images.add(s3Object.readAllBytes());
            } catch (IOException e) {
                throw new UncheckedIOException("Ошибка при получении изображений постеров из облачного хранилища", e);
            }
        });

        return images;
    }

    /**
     * Lists all matched images in S3 folder
     *
     * @param idsSet - required ids
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
     * @param metadataId - content metadata id
     * @param image      - new multipart form data image
     */
    @Transactional
    public void updateExistingImage(Long metadataId, MultipartFile image) {
        posterRepo.updatePosterByContentMetadataId(metadataId,
                image.getOriginalFilename(), image.getContentType());

        final Long s3ImageId = lsPosterStorageFolder(Set.of(metadataId)).getFirst();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(String.valueOf(s3ImageId))
                .contentType(image.getContentType())
                .build();
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Deletes saved poster which matches requested ids from S3 and local db.
     *
     * <p>
     * Also supports single id instance. example: {@code "4"}.
     * </p>
     *
     * @param contentMetadataIds - ids split by ',' separator. For example: {@code "4,2,592,101,10"}
     * @throws CustomNumberFormatException - in case of invalid number format defined by api
     * @throws S3Exception                 - in case image wasn't deleted
     */
    @Transactional
    public void deleteByIds(String contentMetadataIds) {
        Set<Long> idsSet;
        try {
            idsSet = Arrays.stream(contentMetadataIds.split(","))
                    .mapToLong(Long::parseLong)
                    .boxed()
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new CustomNumberFormatException();
        }

        List<Long> s3ImageIds = lsPosterStorageFolder(idsSet);
        s3ImageIds.forEach(id -> {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(FOLDER + id.toString())
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        });
    }

    // public List<ContentMetadata> queryMetadataRepoForIds(int amount) {
    // final Pageable requestedAmountRestriction = PageRequest.of(0, amount);
    // return metadataRepo.findRecentlyAdded(requestedAmountRestriction).stream()
    // .map(ContentMetadata::getPosterId)
    // .toList();
    // }
    //
    // public List<Map<String, byte[]>> getRecentlySavedPosters(int amount) {
    // List<String> recentSavedPosterIds = queryMetadataRepoForIds(amount);
    // List<Map<String, byte[]>> imagesAndMetadata = new ArrayList<>(amount);
    //
    // for (String id : recentSavedPosterIds) {
    // Map<String, byte[]> data = new HashMap<>();
    //
    // Poster poster = posterRepo.findById(id);
    // final ContentMetadata metadata = metadataRepo.getByPosterId(poster.getId());
    // data.put("metadataId", metadata.getId().getBytes());
    // data.put("title", metadata.getTitle().getBytes());
    // data.put("releaseDate", metadata.getReleaseDate().getBytes());
    // data.put("country", metadata.getCountry().getBytes());
    // data.put("mainGenre", metadata.getMainGenre().getBytes());
    // data.put("subGenres", metadata.getSubGenres().toString().replace("[",
    // "").replace("]", "").getBytes());
    // data.put("age", metadata.getAge().toString().getBytes());
    // data.put("rating", String.valueOf(metadata.getRating()).getBytes());
    // data.put("poster", poster.getImage().getData());
    // data.put("videoId", metadata.getVideoId().getBytes());
    //
    // imagesAndMetadata.add(data);
    // }
    // return imagesAndMetadata;
    // }

}
