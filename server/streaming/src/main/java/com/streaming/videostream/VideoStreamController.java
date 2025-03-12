package com.streaming.videostream;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/stream")
public class VideoStreamController {

    private final VideoStreamService service;

    public VideoStreamController(VideoStreamService service) {
        this.service = service;
    }

    @GetMapping("{id}/playlist")
    public void getPlaylist(@PathVariable String id, HttpServletResponse response) {
        service.getHlsPlaylist(response, id);
    }

    @GetMapping("{id}/chunk/{chunkName}")
    public void streamChunk(@PathVariable String id, @PathVariable String chunkName, HttpServletResponse response) {
        String objectKey = VideoStreamService.S3_FOLDER + id + "/" + chunkName;
        service.streamChunk(response, objectKey);
    }
}
