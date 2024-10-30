package ru.storage.objectstorage.poster;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.metadata.ContentMetadata;
import ru.storage.metadata.MetadataRepo;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PosterService {

    @Value("${custom.s3.BUCKET_NAME}")
    private String BUCKET_NAME;
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
        Assert.notNull(BUCKET_NAME, "переменная BUCKET_NAME должна быть указана в конфигурации application.properties.");
    }

    public Poster savePoster(Long metadataId, MultipartFile file) {
        final Poster savedPoster;
        final String contentType = Optional.ofNullable(file.getContentType()).orElseGet(() -> "image/jpeg");
        try {
            ContentMetadata contentMetadata = metadataRepo.findById(metadataId).orElseThrow();
            Poster newPosterInstance = new Poster(file.getName(), contentType, contentMetadata);
            savedPoster = posterRepo.save(newPosterInstance);
        } catch (Exception e) {
            String errMessage = "Метод сохранения плаката не предназначен для работы без ссылки на таблицу метаданных.";
            log.error(errMessage);
            throw new InvalidDataAccessApiUsageException(errMessage);
        }
        try (InputStream inputStream = file.getInputStream()) {
            final String key = "posters/" + metadataId + "-" + file.getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            log.error("Couldn't save poster image in S3 because of {}", e.getMessage());
        }
        return savedPoster;

    }

    public List<byte[]> getImagesMatchingMetadataIds(String contentMetadataIds) {
        List<byte[]> images = new ArrayList<>();
        Set<Long> idsSet = Arrays.stream(contentMetadataIds.split(","))
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toSet());

        ListObjectsRequest lsRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME)
                .prefix(FOLDER)
                .build();
        ListObjectsResponse lsResponse = s3Client.listObjects(lsRequest);

        List<String> matchedImagesNames = lsResponse.contents().stream()
                .map(s3Object -> s3Object.key().substring(FOLDER.length()))
                .filter(name -> idsSet.contains(Long.parseLong(name.split("-")[0])))
                .map(FOLDER::concat)
                .toList();

        matchedImagesNames.forEach(imageName -> {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(imageName)
                    .build();

            // Retrieve the object and add its contents as a byte array to the list
            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
                images.add(s3Object.readAllBytes());
            } catch (IOException e) {
                log.warn("Couldn't retrieve poster image from S3 storage because of {}", e.getMessage());
            }
        });

        return images;
    }

    public void updateExistingImage(Long metadataId, MultipartFile image) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateExistingImage'");
    }

    public void deleteByIds(String contentMetadataIds) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByIds'");
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
