package ru.streaming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ru.streaming.service.VideoService;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

    @GetMapping(value = "/stream/start/{filename}", produces = "video/mp2t")
    public Mono<ResponseEntity<byte[]>> streamVideo(
            @RequestHeader(value = "Range", required = false) String range,
            @PathVariable String filename) {
        // return Mono.just(service.prepareContent(id, range));
        return service.prepareContent(filename, range);
    }

    // @DeleteMapping("/stream/stop/{filename}")
    // public void stopStreaming(@PathVariable String filename) {
    // service.stopStreaming(filename);
    // }

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
