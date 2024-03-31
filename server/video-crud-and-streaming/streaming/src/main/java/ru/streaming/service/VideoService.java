package ru.streaming.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import ru.streaming.controller.VideoController;

import static ru.streaming.constants.ApplicationConstants.DOWNLOAD_CHUNK_SIZE;
import static ru.streaming.constants.ApplicationConstants.VIDEO_STORAGE_PATH;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
public class VideoService {
    @Autowired
    private ReactiveMongoOperations mongoOperations;
    @Autowired
    private ResourceLoader resourceLoader;

    private static final Logger log = LoggerFactory.getLogger(VideoController.class);

    public Mono<ResponseEntity<byte[]>> prepareContent(final String filename, final String range) {
//            return range == null ?
//                    contentFromBeginning(binaryContent) :
//                    contentFromRange(binaryContent, range);
        String filepath = "classpath:/videos/sample.mp4";
        return Mono.fromSupplier(() -> {
                    try {
                        return resourceLoader.getResource(filepath).getContentAsByteArray();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(byteArray -> range.isEmpty() ? contentFromBeginning(byteArray) : contentFromRange(byteArray, range));
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

    private ResponseEntity<byte[]> contentFromBeginning(byte[] binaryContent) {
        long rangeStart = 1;
        long rangeEnd = DOWNLOAD_CHUNK_SIZE;

        final long fileSize = binaryContent.length;
        final String contentRange = "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize;
        HttpHeaders headers = composeHeaders(rangeEnd, contentRange);
        HttpStatus httpStatus = HttpStatus.PARTIAL_CONTENT;

        byte[] binaryVideoContent = readByteRange(binaryContent, rangeStart, rangeEnd);
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

}
