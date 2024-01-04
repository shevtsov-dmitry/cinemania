package com.videostorage.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.sun.net.httpserver.Headers;
import com.videostorage.model.Video;
import com.videostorage.repo.VideoRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.videostorage.constants.ApplicationConstants.*;

@Service
public class VideoService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private final VideoRepo repo;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations operations;

    public VideoService(VideoRepo repo, GridFsTemplate gridFsTemplate, GridFsOperations operations) {
        this.repo = repo;
        this.gridFsTemplate = gridFsTemplate;
        this.operations = operations;
    }

    public String saveVideo(String title, MultipartFile file) throws IOException {
        Video video = new Video();
        video.setTitle(title);
        video.setContentType(file.getContentType());
        repo.save(video);
        gridFsTemplate.store(file.getInputStream(), title, file.getContentType());
        return "new video saved: %s".formatted(video);
    }

    public String deleteVideo(@PathVariable String title) {
        if (!repo.existsByTitle(title)) {
            return "impossible to delete video.";
        }
        Query.query(Criteria.where("filename").is(title));
        return STR."video \{title} has been deleted successfully.";
    }

    public ResponseEntity<byte[]> prepareContent(final String title, final String range) {
        long rangeStart = 0;
        long rangeEnd = CHUNK_SIZE;
        // TODO: fix the retrieval of the whole video file from gridfs. Need to retrieve small chunks of video.
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("filename").is(title)));
        var outputStream = new ByteArrayOutputStream();
        try {
            operations.getResource(Objects.requireNonNull(file)).getInputStream().transferTo(outputStream);
        } catch (IOException | NullPointerException e) {
            return ResponseEntity.badRequest().body("Impossible to retrieve video byte range.".getBytes());
        }
        byte[] binaryContent = outputStream.toByteArray();
        var resource = new ByteArrayResource(binaryContent);
        final long fileSize = resource.contentLength();
        HttpStatus httpStatus = HttpStatus.PARTIAL_CONTENT;
        String contentLength = String.valueOf(rangeEnd);
        Map<String, String> headers = new HashMap<>(
                Map.of(
                        CONTENT_TYPE, VIDEO_CONTENT,
                        ACCEPT_RANGES, BYTES,
                        CONTENT_LENGTH, contentLength,

                )
        )
        final String contentRange = BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize;
        if (range == null) {
            return response(httpStatus, contentLength, contentRange, binaryContent, rangeStart, rangeEnd);
        }
        String[] ranges = range.split("-");
        rangeStart = Long.parseLong(ranges[0].substring(6));
        rangeEnd = ranges.length > 1 ? Long.parseLong(ranges[1]) : rangeStart + rangeEnd;
        rangeEnd = Math.min(rangeEnd, fileSize - 1);
        contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        httpStatus = rangeEnd >= fileSize ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT;
        return response(httpStatus, contentLength, contentRange, binaryContent, rangeStart, rangeEnd);
    }

    private ResponseEntity<byte[]> response(HttpStatus httpStatus, String contentLength, String contentRange,
                                            byte[] binaryContent, long rangeStart, long rangeEnd) {
        return ResponseEntity.status(httpStatus)
                .header(CONTENT_TYPE, VIDEO_CONTENT)
                .header(ACCEPT_RANGES, BYTES)
                .header(CONTENT_LENGTH, contentLength)
                .header(CONTENT_RANGE, contentRange)
                .body(readByteRangeNew(binaryContent, rangeStart, rangeEnd));
    }


    public byte[] readByteRangeNew(byte[] binaryContent, long start, long end) {
        byte[] result = new byte[(int) (end - start) + 1];
        System.arraycopy(binaryContent, (int) start, result, 0, (int) (end - start) + 1);
        return result;
    }

}
