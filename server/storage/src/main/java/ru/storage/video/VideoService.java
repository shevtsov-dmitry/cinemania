package ru.storage.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

@Service
public class VideoService {

    @Value("${custom.VIDEO_STORAGE_PATH}")
    private String VIDEO_STORAGE_PATH;

    public String uploadVideo(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Path uploadDirectory = Paths.get(VIDEO_STORAGE_PATH);
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadDirectory.resolve();
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload video file: %s".formatted(fileName), e);
        }
    }
}
