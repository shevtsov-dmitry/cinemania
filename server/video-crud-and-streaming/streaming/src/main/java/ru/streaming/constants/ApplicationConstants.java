package ru.streaming.constants;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApplicationConstants {
    public static final int CHUNK_SIZE = 3 * 1024 * 1024; // MB
    public static final String BUCKET_NAME = "video_storage_gridfs";


}
