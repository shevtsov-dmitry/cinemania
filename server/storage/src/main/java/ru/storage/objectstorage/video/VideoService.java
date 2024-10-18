package ru.storage.objectstorage.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoService {

    @Value("${custom.VIDEO_STORAGE_PATH: }")
    private String VIDEO_STORAGE_PATH;

    public String uploadVideo(MultipartFile file) {

        // TODO implement S3 save
        return null;
    }
}
