package ru.streaming.controller;

import ru.streaming.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
