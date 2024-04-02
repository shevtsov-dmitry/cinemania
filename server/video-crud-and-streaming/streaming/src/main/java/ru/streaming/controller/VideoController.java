package ru.streaming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.streaming.service.VideoService;

import java.io.File;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

    @GetMapping(value = "/stream/{filename}", produces = "video/mp4")
    public Mono<ResponseEntity<byte[]>> streeamVideo(
            @RequestHeader(value = "Range", required = false) String range,
            @PathVariable String filename) {
        // return Mono.just(service.prepareContent(id, range));
        return service.prepareContent(filename, range);
    }

    // private static HttpHeaders composeHeaders(long contentLength, String
    // contentRange) {
    // HttpHeaders headers = new HttpHeaders();
    // headers.setContentType(MediaType.valueOf("video/mp4"));
    // headers.setContentLength(contentLength);
    // headers.set("Accept-Ranges", "bytes");
    // headers.set("Content-Range", contentRange);
    // return headers;
    // }

    // @GetMapping(value = "/get/chunk", produces = "video/mp4")

}
