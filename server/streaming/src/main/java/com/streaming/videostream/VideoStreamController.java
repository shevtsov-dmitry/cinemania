package com.streaming.videostream;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@RestController
@RequestMapping("api/v1/stream")
public class VideoStreamController {

  private final S3Client s3Client;
  private static final Logger LOG = LoggerFactory.getLogger(VideoStreamController.class);

  @Value("${custom.s3.BUCKET_NAME}")
  private String bucketName;

  private static final String S3_FOLDER = "videos/trailer/";

  public VideoStreamController(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  @GetMapping("{id}/playlist")
  public void getPlaylist(@PathVariable String id, HttpServletResponse response) {
    String objectKey = S3_FOLDER + id + "/index.m3u8";

    try {
      ResponseBytes<?> objectBytes =
          s3Client.getObjectAsBytes(
              GetObjectRequest.builder().bucket(bucketName).key(objectKey).build());

      String playlistContent = new String(objectBytes.asByteArray());

      String baseUrl = "http://localhost:8443/api/v1/stream/" + id + "/chunk/";
      playlistContent = playlistContent.replaceAll("(index\\d+\\.ts)", baseUrl + "$1");

      response.setContentType("application/vnd.apple.mpegurl");
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"index.m3u8\"");
      response.getWriter().write(playlistContent);
    } catch (IOException e) {
      LOG.error("Error serving M3U8 playlist: {}", e.getMessage());
    }
  }

  @GetMapping("{id}/chunk/{chunkName}")
  public void getChunk(
      @PathVariable String id, @PathVariable String chunkName, HttpServletResponse response) {
    String objectKey = S3_FOLDER + id + "/" + chunkName;

    try {
      ResponseBytes<?> objectBytes =
          s3Client.getObjectAsBytes(
              GetObjectRequest.builder().bucket(bucketName).key(objectKey).build());

      response.setContentType("video/MP2T"); // Correct MIME type for TS chunks
      response.getOutputStream().write(objectBytes.asByteArray());
    } catch (IOException e) {
      LOG.error("Error serving TS chunk: {}", e.getMessage());
    }
  }
}
