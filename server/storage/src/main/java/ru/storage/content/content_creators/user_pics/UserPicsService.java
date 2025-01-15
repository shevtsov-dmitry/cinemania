package ru.storage.content.content_creators.user_pics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnailator;
import ru.storage.content.common.S3GeneralOperations;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class UserPicsService {

    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;
    private final S3Client s3Client;

    private static final Logger log = LoggerFactory.getLogger(UserPicsService.class);
    private static final String S3_FOLDER = "user_pics";

    public UserPicsService (S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadUserPic(String id, MultipartFile image) {
        assureImageProcessing(image.getContentType());
        try (InputStream inputStream = image.getInputStream()) {
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(S3_FOLDER + "/" + id)
                    .contentType(image.getContentType())
                    .build();
            InputStream compressedImage = compressImage(inputStream);
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(compressedImage, compressedImage.available()));
            compressedImage.close();
        } catch (IOException | AwsServiceException e) {
            String errmes = "Ошибка при сохранении фотографии профиля.";
            log.warn("{}. {}", errmes, e.getMessage());
            throw S3Exception.builder()
                    .message(errmes)
                    .build();
        }
    }
    
    public List<Pair<String, byte[]>> getUserPics(String ids) {
        return S3GeneralOperations.getItemsByIds(S3_FOLDER, ids);
    }
    
    public void deleteUserPic(String id) {
        S3GeneralOperations.deleteItems(S3_FOLDER, id);
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
            Thumbnailator.createThumbnail(inputStream, outStream, 225, 300);
            return outStream.getInputStream();
        } catch (IOException e) {
            log.error("Ошибка при сжатии файла изображения. Вызвана: {}", e.getMessage());
            return inputStream;
        }
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
            log.warn(errmes);
            throw new IllegalArgumentException(errmes);
        }
    }


}
