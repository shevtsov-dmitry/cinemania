package ru.streaming.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsOperations;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import ru.streaming.controller.VideoController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static ru.streaming.constants.ApplicationConstants.DOWNLOAD_CHUNK_SIZE;

@Service
public class VideoService {
    @Autowired
    private ReactiveMongoOperations mongoOperations;
    @Autowired
    private  ReactiveGridFsOperations operations;
    @Autowired
    private ReactiveGridFsTemplate gridFsTemplate;
    private static final Logger log = LoggerFactory.getLogger(VideoController.class);


//    public ResponseEntity<byte[]> prepareContent(final String id, final String range) {
//        try {
//            byte[] binaryContent = getFileAsBinary(id);
//            return range == null ?
//                    contentFromBeginning(binaryContent) :
//                    contentFromRange(binaryContent, range);
//        } catch (IOException | NullPointerException e) {
//            return ResponseEntity.badRequest().body("Impossible to retrieve video byte range.".getBytes());
//        }
//    }
//
//    private ResponseEntity<byte[]> contentFromBeginning(byte[] binaryContent) {
//        long rangeStart = 1;
//        long rangeEnd = DOWNLOAD_CHUNK_SIZE;
//
//        final long fileSize = binaryContent.length;
//        final String contentRange = "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize;
//        HttpHeaders headers = composeHeaders(rangeEnd, contentRange);
//        HttpStatus httpStatus = HttpStatus.PARTIAL_CONTENT;
//
//        byte[] binaryVideoContent = readByteRange(binaryContent, rangeStart, rangeEnd);
//        return response(httpStatus, headers, binaryVideoContent);
//    }
//
//    private ResponseEntity<byte[]> contentFromRange(byte[] binaryContent, String range) {
//        final long fileSize = binaryContent.length;
//        String[] ranges = range.split("-");
//        long rangeStart = Long.parseLong(ranges[0].substring(6));
//        long rangeEnd = ranges.length > 1 ? Long.parseLong(ranges[1]) : rangeStart + DOWNLOAD_CHUNK_SIZE;
//        rangeEnd = Math.min(rangeEnd, fileSize - 1);
//
//        long contentLength = (rangeEnd - rangeStart) + 1;
//        final String contentRange = "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize;
//        HttpHeaders headers = composeHeaders(contentLength, contentRange);
//
//        byte[] binaryVideoContent = readByteRange(binaryContent, rangeStart, rangeEnd);
//        HttpStatus httpStatus = rangeEnd >= fileSize ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT;
//        return response(httpStatus, headers, binaryVideoContent);
//    }
//
//    private ResponseEntity<byte[]> response(HttpStatus httpStatus, HttpHeaders headers, byte[] binaryVideoContent) {
//        return ResponseEntity.status(httpStatus)
//                .headers(headers)
//                .body(binaryVideoContent);
//    }
//
//    private byte[] getFileAsBinary(String id) throws IOException, NullPointerException {
//        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
//        Assert.notNull(file, "FILE IS NULL");
//        var outputStream = new ByteArrayOutputStream();
//        operations.getResource(Objects.requireNonNull(file)).getInputStream().transferTo(outputStream);
//        return outputStream.toByteArray();
//    }
//
//    private static HttpHeaders composeHeaders(long contentLength, String contentRange) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.valueOf("video/mp4"));
//        headers.setContentLength(contentLength);
//        headers.set("Accept-Ranges", "bytes");
//        headers.set("Content-Range", contentRange);
//        return headers;
//    }
//
//    public byte[] readByteRange(byte[] binaryContent, long start, long end) {
//        byte[] result = new byte[(int) (end - start) + 1];
//        System.arraycopy(binaryContent, (int) start, result, 0, (int) (end - start) + 1);
//        return result;
//    }

    public Flux<Document> getChunksFlux() {
        String files_id = "65ef1bc446b83239f8382e54";
        long fullLength = 15268651;
        long chunkSize = 261120;

        long headerStart = 14352385;
        long headerEnd = 15268650;

        long startChunkN = headerStart / chunkSize;
        long endChunkN = headerEnd / chunkSize;

        Document fileMetadata = mongoOperations.findOne(
                Query.query(Criteria.where("_id").is(new ObjectId(files_id))),
                Document.class,
                "fs.files"
        ).block();

        int totalChunks = fileMetadata.getInteger("chunkCount");

        Flux<Document> chunksFlux = Flux.range(0, totalChunks)
                .flatMap(chunkNumber -> {
                    Query query = Query.query(
                            Criteria.where("files_id").is(new ObjectId(files_id))
                                    .and("n").is(chunkNumber)
                    );
                    return mongoOperations.find(query, Document.class, "fs.chunks");
                });

        return chunksFlux;
    }
}