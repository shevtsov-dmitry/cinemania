package com.streaming.videostream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

@RestController
@RequestMapping("api/v1/stream")
public class VideoStreamController {

    private final VideoStreamService service;
    private final S3Client s3Client;

    public VideoStreamController(VideoStreamService service, S3Client s3Client) {
        this.service = service;
        this.s3Client = s3Client;
    }

    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;
    private final String S3_FOLDER = "videos";

    @GetMapping("{filename:.+}")
    public void streamVideo(
            @PathVariable String filename,
            HttpServletRequest request,
            HttpServletResponse response) {

        // Construct the S3 object key
        String objectKey = S3_FOLDER + "/standalone/" + filename;
        System.out.println(objectKey);

        // Get file metadata
        var headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        var headResponse = s3Client.headObject(headRequest);

        long fileSize = headResponse.contentLength();
        String contentType = headResponse.contentType();

        // Determine the range of bytes to serve
        String rangeHeader = request.getHeader(HttpHeaders.RANGE);
        long start = 0;
        long end = fileSize - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            start = Long.parseLong(ranges[0]);
            if (ranges.length > 1) {
                end = Long.parseLong(ranges[1]);
            }
        }

        long contentLength = end - start + 1;

        // Prepare the GetObjectRequest
        var getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .range("bytes=" + start + "-" + end)
                .build();

        // Fetch the requested bytes
        ResponseBytes<?> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

        // Set response headers
        response.setContentType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);

        response.setStatus(
                rangeHeader == null ? HttpServletResponse.SC_OK : HttpServletResponse.SC_PARTIAL_CONTENT);

        // Stream the content to the response
        try (InputStream inputStream = objectBytes.asInputStream();
                OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[8192]; // 8KB buffer
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error streaming video", e);
        }

    }

    private MediaType determineMediaType(String filename) {
        if (filename.endsWith(".m3u8")) {
            return MediaType.valueOf("application/vnd.apple.mpegurl");
        } else if (filename.endsWith(".ts")) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        return MediaType.APPLICATION_OCTET_STREAM; // Default for unknown types
    }

}
