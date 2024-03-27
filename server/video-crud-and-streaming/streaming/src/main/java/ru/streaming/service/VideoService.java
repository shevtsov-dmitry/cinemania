package ru.streaming.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static ru.streaming.constants.ApplicationConstants.CHUNK_SIZE;
import static ru.streaming.constants.ApplicationConstants.BUCKET_NAME;

@Service
public class VideoService {
    private final GridFsTemplate gridFsTemplate;
    @Autowired
    private MongoOperations mongoOperations;
    private final GridFsOperations operations;

    Map<String, byte[]> streamedVideos = new HashMap<>();

    @Autowired
    public VideoService(GridFsTemplate gridFsTemplate, GridFsOperations operations) {
        this.gridFsTemplate = gridFsTemplate;
        this.operations = operations;
    }

    public ResponseEntity<byte[]> prepareContent(final String id, final String range) {
        try {
            byte[] binaryContent = getFileAsBinary(id);
            System.out.println(binaryContent.length);
            return range == null ?
                    contentFromBeginning(binaryContent) :
                    contentFromRange(binaryContent, range);
        } catch (IOException | NullPointerException e) {
            return ResponseEntity.badRequest().body("Impossible to retrieve video byte range.".getBytes());
        }
    }

    private ResponseEntity<byte[]> contentFromRange(byte[] binaryContent, String range) {
        final long fileSize = binaryContent.length;
        String[] ranges = range.split("-");
        long rangeStart = Long.parseLong(ranges[0].substring(6));
        long rangeEnd = ranges.length > 1 ? Long.parseLong(ranges[1]) : rangeStart + CHUNK_SIZE;
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
        long rangeEnd = CHUNK_SIZE;

        final long fileSize = binaryContent.length;
        final String contentRange = "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize;
        HttpHeaders headers = composeHeaders(rangeEnd, contentRange);
        HttpStatus httpStatus = HttpStatus.PARTIAL_CONTENT;

        byte[] binaryVideoContent = readByteRange(binaryContent, rangeStart, rangeEnd);
        return response(httpStatus, headers, binaryVideoContent);
    }

    private byte[] getFileAsBinary(String id) throws IOException, NullPointerException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        Assert.notNull(file, "FILE IS NULL");
        var outputStream = new ByteArrayOutputStream();
        operations.getResource(Objects.requireNonNull(file)).getInputStream().transferTo(outputStream);
        return outputStream.toByteArray();
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

    public String getOneChunk() {
        String id = "65ef1bc446b83239f8382e5c";
        List<Document> file = mongoOperations.find(
                Query.query(Criteria.where("_id").is(id)),
                Document.class,
                "fs.chunks"
        );
        return file.toString();
    }
}