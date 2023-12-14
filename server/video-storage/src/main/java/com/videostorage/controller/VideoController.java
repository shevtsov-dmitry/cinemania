package com.videostorage.controller;

import com.videostorage.model.Video;
import com.videostorage.service.VideoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

    @PostMapping("/upload/one")
    public ResponseEntity<String> saveVideo(@RequestParam String name, @RequestParam MultipartFile file) {
        try {
            String successMessage = service.saveVideo(name, file);
            return ResponseEntity.status(200).body(successMessage);
        } catch (IOException exception) {
            exception.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading video");
        }
    }

    @GetMapping(value = "/get/one/byTitle/{title}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Video sendVideoResponse(@PathVariable String title) throws IOException {
        return service.getVideo(title);
    }

    @GetMapping(value = "/delete/one/byTitle/{name}")
    public String deleteVideo(@PathVariable String name) {
        return service.deleteVideo(name);
    }

    @GetMapping("/stream/{title}")
    public void streamVideo(@PathVariable String title,
                            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader,
                            HttpServletResponse response) throws Exception {
        Video video = service.getVideo(title);
        InputStream videoStream = video.getStream();
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if (rangeHeader != null && rangeHeader.startsWith("bytes")) {
            // Parse the range header
            long[] range = parseRangeHeader(rangeHeader, videoStream.available());

            // Set the appropriate headers for a partial content response
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader(HttpHeaders.CONTENT_RANGE, STR."bytes \{range[0]}-\{range[1]}/\{videoStream.available()}");
            videoStream.skip(range[0]);
        }

        FileCopyUtils.copy(video.getStream(), response.getOutputStream());
    }

    // Helper method to parse the Range header
    private long[] parseRangeHeader(String rangeHeader, long videoLength) {
        String[] rangeValues = rangeHeader.substring("bytes=".length()).split("-");
        long start = Long.parseLong(rangeValues[0]);
        long end = rangeValues.length > 1 ? Long.parseLong(rangeValues[1]) : videoLength - 1;

        return new long[]{start, end};
    }
}
