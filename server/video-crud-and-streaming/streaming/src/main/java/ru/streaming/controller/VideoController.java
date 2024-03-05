package ru.streaming.controller;

import org.springframework.http.MediaType;
import ru.streaming.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.awt.*;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

    @GetMapping(value = "/stream/{id}")
    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String range,
                                                    @PathVariable String id) {
        return Mono.just(service.prepareContent(id, range));
    }

}
