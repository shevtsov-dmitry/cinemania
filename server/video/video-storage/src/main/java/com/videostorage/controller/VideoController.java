package com.videostorage.controller;

import com.videostorage.model.Video;
import com.videostorage.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

    // TODO: add support for streaming to mobile devices
    @GetMapping("/stream/{title}")
    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String range,
                                                    @PathVariable String title) {
        return Mono.just(service.prepareContent(title, range));
    }

}
