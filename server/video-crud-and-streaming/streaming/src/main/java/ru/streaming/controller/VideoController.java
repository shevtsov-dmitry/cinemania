package ru.streaming.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.streaming.model.VideoChunkMetadata;
import ru.streaming.service.VideoService;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final static Logger LOG = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

    @GetMapping("/stream/start/{filename}")
    public ResponseEntity<byte[]> streamVideo(
            @RequestHeader(value = "Range", required = false) String range,
            @PathVariable String filename) {
        LOG.debug("Accepted Range: {}", range);
        try {
            byte[] videoChunkBytes = service.prepareContent(filename, range);
            VideoChunkMetadata metadata = service.getVideoChunkMetadata();
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp2t")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metadata.contentLength()))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE, "bytes %d-%d/%d"
                            .formatted(metadata.start(), metadata.end(), metadata.fileSize()))
//                    .header(HttpHeaders.TRANSFER_ENCODING, "chunked")
                    .body(videoChunkBytes);
        } catch (FileNotFoundException e) {
            return ResponseEntity.badRequest().body("COULDN'T FIND FILE IN PATH. %s".formatted(e.getMessage()).getBytes());
        }
    }
}
    // @DeleteMapping("/stream/stop/{filename}")
    // public void stopStreaming(@PathVariable String filename) {
    // service.stopStreaming(filename);
    // }

    // @GetMapping(value = "/get/chunk", produces = "video/mp4")

