package ru.streaming.service;

import static ru.streaming.constants.ApplicationConstants.DOWNLOAD_CHUNK_SIZE;
import static ru.streaming.constants.ApplicationConstants.VIDEO_STORAGE_PATH;

import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class VideoService {
    // private final long ONE_MB = (long) Math.pow(2, 20);
    // private static final int CHUNK_SIZE = 3;
    private static final Logger LOG = LoggerFactory.getLogger(VideoService.class);

    public Mono<ResponseEntity<byte[]>> prepareContent(String filename, String range) {
        String videoPath = "%s%s.ts".formatted(VIDEO_STORAGE_PATH, filename);

        String[] rangesCouple = range.substring(6).split("-");
        long startRange = Long.parseLong(rangesCouple[0]);
        long endRange = startRange + DOWNLOAD_CHUNK_SIZE;
        if (rangesCouple.length != 1)
            endRange = Long.parseLong(rangesCouple[1]);

        try (var fStream = new FileInputStream(videoPath)) {
            byte[] buffer = new byte[(int) (endRange - startRange)];
            fStream.skipNBytes(startRange);
            fStream.read(buffer, 0, buffer.length);
            HttpHeaders headers = composeHttpHeaders(buffer, startRange, endRange);
            return Mono.just(new ResponseEntity<>(buffer, headers, HttpStatus.PARTIAL_CONTENT));
        } catch (IOException e) {
            LOG.error("CANNOT FIND FILE IN PATH: {}", videoPath);
            e.printStackTrace();
            return Mono.just(
                    ResponseEntity.internalServerError()
                            .body("CANNOT FIND FILE IN PATH: %s".formatted(videoPath).getBytes()));
        }

    }

    private HttpHeaders composeHttpHeaders(byte[] buffer, long startRange, long endRange) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4"));
        headers.setContentLength(buffer.length);
        headers.set("Accept-Ranges", "bytes");
        headers.set("Content-Range", "bytes=%d-%d".formatted(startRange, endRange));
        return headers;
    }

}
