package ru.streaming.constants;

public class ApplicationConstants {
    // TODO make it env
    public static final int DOWNLOAD_CHUNK_SIZE = 3 * (int) Math.pow(2, 20); // MB
    public static final String BUCKET_NAME = "preview_images_storage";
    public static final String VIDEO_STORAGE_PATH = "/mnt/HDD/NginxVideos";

}
