package ru.streaming.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApplicationConstants {
    public static final int DOWNLOAD_CHUNK_SIZE = 3 * 1024 * 1024; // MB
    public static final String BUCKET_NAME = "video_storage_gridfs";
    public static final String VIDEO_STORAGE_PATH = "/home/shd/Videos/";
}
