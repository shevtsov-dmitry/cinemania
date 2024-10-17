package com.cinemania.videostream;

import java.net.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/videostream")
public class VideoStreamController {

    private final VideoStreamService service;

    public VideoStreamController(VideoStreamService service) {
        this.service = service;
    }

    // @GetMapping(value = "/stream", produces = )
    // public ResponseEntity<> stream() {
    // HttpHeaders headers = service.getHttpHeadersByVideoId(videoId);
    // return ResponseEntity.ok();
    // }

}
