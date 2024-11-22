package ru.storage.metadata.objectstorage.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

    private final S3Client s3Client;

    public VideoService(S3Client s3Client) {
        this.s3Client = s3Client;
    }


    public String uploadVideo(MultipartFile file) {
        // TODO implement S3 save
        return null;
    }

    public String sayHello() {
        return getFileContent("videos121212", "message.txt");
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
