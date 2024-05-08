package ru.streaming.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.streaming.model.VideoChunkMetadata;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static ru.streaming.constants.ApplicationConstants.DOWNLOAD_CHUNK_SIZE;
import static ru.streaming.constants.ApplicationConstants.VIDEO_STORAGE_PATH;

@Service
public class VideoService {

    private static final Logger LOG = LoggerFactory.getLogger(VideoService.class);
    private VideoChunkMetadata metadata;

    public byte[] prepareContent(String filename, String range) throws FileNotFoundException {
        final String videoPath = "%s%s.m2ts".formatted(VIDEO_STORAGE_PATH, filename);
        String[] rangesCouple = range.substring(6).split("-");
        long startRange = Long.parseLong(rangesCouple[0]);
        long endRange = startRange + DOWNLOAD_CHUNK_SIZE;
        if (rangesCouple.length != 1)
            endRange = Long.parseLong(rangesCouple[1]);

        try (var fStream = new FileInputStream(videoPath)) {
            this.metadata = new VideoChunkMetadata(startRange, endRange, endRange - startRange, fStream.available());
            fStream.skip(startRange);
            byte[] buffer = new byte[(int) (endRange - startRange)];
            fStream.skipNBytes(startRange);
            fStream.read(buffer, 0, buffer.length);
            return buffer;

        } catch (IOException e) {
            throw new FileNotFoundException(videoPath);
        }
    }


    public VideoChunkMetadata getVideoChunkMetadata() {
        return this.metadata;
    }
}

