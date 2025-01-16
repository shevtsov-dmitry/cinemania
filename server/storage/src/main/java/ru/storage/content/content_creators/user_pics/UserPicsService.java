package ru.storage.content.content_creators.user_pics;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnailator;
import ru.storage.content.common.BinaryContentUtils;
import ru.storage.content.common.S3GeneralOperations;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
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
        BinaryContentUtils.assureImageProcessing(image.getContentType());
        try (InputStream inputStream = image.getInputStream()) {
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(S3_FOLDER + "/" + id)
                    .contentType(image.getContentType())
                    .build();
            InputStream compressedImage = BinaryContentUtils.compressImage(inputStream);
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

}
