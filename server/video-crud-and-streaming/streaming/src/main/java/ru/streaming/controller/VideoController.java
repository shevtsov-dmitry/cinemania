package ru.streaming.controller;

import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.streaming.service.VideoService;

import java.nio.ByteBuffer;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

//    @GetMapping(value = "/stream/{id}")
//    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String range,
//                                                    @PathVariable String id) {
//
//        return Mono.just(service.prepareContent(id, range));
//    }

    @GetMapping(value = "/get/chunk", produces = "video/mp4")
    public ResponseEntity<Flux<ByteBuffer>> streamVideo() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4"));
        headers.set("Accept-Ranges", "bytes");

        Flux<Document> chunksFlux = service.getChunksFlux(); // From step 2

        Flux<ByteBuffer> videoStream = chunksFlux.map(chunk -> {
            Binary binaryData = (Binary) chunk.get("data");
            return ByteBuffer.wrap(binaryData.getData());
        });

        return ResponseEntity.ok()
                .headers(headers)
                .body(videoStream);
    }

}
