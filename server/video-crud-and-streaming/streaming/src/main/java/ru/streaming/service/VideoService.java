package ru.streaming.service;

import static ru.streaming.constants.ApplicationConstants.DOWNLOAD_CHUNK_SIZE;
import static ru.streaming.constants.ApplicationConstants.VIDEO_STORAGE_PATH;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import ru.streaming.controller.VideoController;

@Service
public class VideoService {
    private static final Map<String, Mono<byte[]>> loadedVideos = new ConcurrentHashMap<>();
    // TODO delete video from loadedVideos if user doesn't watch video no more
    private static final Map<String, Integer> currentViewersForVideo = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(VideoController.class);

    public Mono<ResponseEntity<byte[]>> prepareContent(final String filename, final String range) {
        String filepath = "%s%s.mp4".formatted(VIDEO_STORAGE_PATH, filename);
        if (!loadedVideos.containsKey(filename)) {
            Mono<byte[]> video = Mono.fromSupplier(() -> {
                try {
                    return Files.readAllBytes(Paths.get(filepath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            loadedVideos.put(filename, video);
        }
        log.info("LOADED FILES IN MEMORY: {}", loadedVideos.size());
        return loadedVideos.get(filename)
                .map(byteArray -> contentFromRange(byteArray, range));
    }

    private ResponseEntity<byte[]> contentFromRange(byte[] binaryContent, String range) {
        final long fileSize = binaryContent.length;
        String[] ranges = range.split("-");
        long rangeStart = Long.parseLong(ranges[0].substring(6));
        long rangeEnd = ranges.length > 1 ? Long.parseLong(ranges[1]) : rangeStart + DOWNLOAD_CHUNK_SIZE;
        rangeEnd = Math.min(rangeEnd, fileSize - 1);

        long contentLength = (rangeEnd - rangeStart) + 1;
        final String contentRange = "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize;
        HttpHeaders headers = composeHeaders(contentLength, contentRange);

        byte[] binaryVideoContent = readByteRange(binaryContent, rangeStart, rangeEnd);
        HttpStatus httpStatus = rangeEnd >= fileSize ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT;
        return response(httpStatus, headers, binaryVideoContent);
    }

    private ResponseEntity<byte[]> response(HttpStatus httpStatus, HttpHeaders headers, byte[] binaryVideoContent) {
        return ResponseEntity.status(httpStatus)
                .headers(headers)
                .body(binaryVideoContent);
    }

    private static HttpHeaders composeHeaders(long contentLength, String contentRange) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4"));
        headers.setContentLength(contentLength);
        headers.set("Accept-Ranges", "bytes");
        headers.set("Content-Range", contentRange);
        return headers;
    }

    public byte[] readByteRange(byte[] binaryContent, long start, long end) {
        byte[] result = new byte[(int) (end - start) + 1];
        System.arraycopy(binaryContent, (int) start, result, 0, (int) (end - start) + 1);
        return result;
    }

    // ? how can I stop streaming without disabling stream for other stream watchers
    // ?
    // public void stopStreaming(String filename) {
    // loadedVideos.remove(filename);
    // }
}
