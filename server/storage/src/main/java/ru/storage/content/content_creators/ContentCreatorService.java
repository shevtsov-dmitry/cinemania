package ru.storage.content.content_creators;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnailator;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class ContentCreatorService {

    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;
    private final ActorRepo actorRepo;
    private final DirectorRepo directorRepo;
    private final S3Client s3Client;
    private static final String S3_FOLDER = "contnent-creators-user-pic";
    private static final Logger LOG = LoggerFactory.getLogger(ContentCreatorService.class);

    public ContentCreatorService(ActorRepo actorRepo, DirectorRepo directorRepo, S3Client s3Client) {
        this.actorRepo = actorRepo;
        this.directorRepo = directorRepo;
        this.s3Client = s3Client;
    }

	public String addCreator(ContentCreator creator) {
	   return switch(creator.contentCreatorKind) {
				case DIRECTOR -> directorRepo.save((Director) creator).getId();
				case ACTOR -> actorRepo.save((Actor) creator).getId();
				default -> throw new IllegalAccessError("Необходимо указать должность создателя контента.");
			};
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
                   s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(compressedImage, compressedImage.available()));
                   compressedImage.close();
               } catch (IOException | AwsServiceException e) {
                   String errmes = "Ошибка при сохранении фотографии профиля.";
                   LOG.warn("{}. {}", errmes, e.getMessage());
                   throw S3Exception.builder()
                           .message(errmes)
                           .build();
               }
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
            LOG.error("Ошибка при сжатии файла изображения. Вызвана: {}", e.getMessage());
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
            LOG.warn(errmes);
            throw new IllegalArgumentException(errmes);
        }
    }

}
