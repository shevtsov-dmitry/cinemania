package ru.storage.metadata.objectstorage.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.metadata.objectstorage.exceptions.NoMetadataRelationException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class VideoService {

//    @Value("${cloud.aws.s3.bucketName}")
//    private String bucketName;

    private static final Logger LOG = LoggerFactory.getLogger(VideoService.class);

    private final S3Client s3Client;
    private final VideoRepo videoRepo;

    public VideoService(S3Client s3Client, VideoRepo videoRepo) {
        this.s3Client = s3Client;
        this.videoRepo = videoRepo;
    }

    public Video saveMetadata(Video video) {
        try {
            return videoRepo.save(video);
        } catch (NoMetadataRelationException e) {
            String errmes = "Метод сохранения плаката не предназначен для работы без ссылки на таблицу метаданных, которая осуществляется по ID.";
            LOG.warn(errmes);
            throw new NoMetadataRelationException(errmes);
        }
    }

    public String uploadVideo(MultipartFile file) {
        return null;
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
