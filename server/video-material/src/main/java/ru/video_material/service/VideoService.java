package ru.video_material.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.StaticValues;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

@Service
public class VideoService {

    public String uploadVideo(MultipartFile file) {
        final String id = generateId();
        String fileName = file.getOriginalFilename();
        Path uploadDirectory = Paths.get(StaticValues.VIDEO_STORAGE_PATH);
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadDirectory.resolve(id.concat(".mp4"));
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload video file: %s".formatted(fileName), e);
        }
    }

    private static String generateId() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        Random random = new Random();
        int idLength = 24;
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < idLength; i++) {
            id.append(chars[random.nextInt(chars.length)]);
        }
        return id.toString();
    }
}
