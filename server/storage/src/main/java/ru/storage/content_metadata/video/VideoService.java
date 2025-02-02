package ru.storage.content_metadata.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.content_metadata.trailer.Trailer;
import ru.storage.exceptions.ParseIdException;
import ru.storage.utils.BinaryContentUtils;
import ru.storage.utils.S3GeneralOperations;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class VideoService {

  @Value("${custom.s3.BUCKET_NAME}")
  private String bucketName;

  private static final String S3_FOLDER = "videos";
  private static final Logger LOG = LoggerFactory.getLogger(VideoService.class);

  private final S3Client s3Client;
  private final VideoRepo videoRepo;

  public VideoService(S3Client s3Client, VideoRepo videoRepo) {
    this.s3Client = s3Client;
    this.videoRepo = videoRepo;
  }

  /**
   * Upload video to s3 storage.
   *
   * @param video video multipart file from the form
   * @throws IllegalArgumentException when multipart file is not a video
   * @throws ParseException when failed to parse video to HLS chunks with ffmpeg
   * @throws IOException when error allocating space to new video file
   * @throws S3Exception when error uploading file to S3
   * @return Video object with metadata saved in database
   */
  public StandaloneVideoShow uploadStandaloneVideoShow(MultipartFile video)
      throws ParseException, IOException {
    BinaryContentUtils.assureVideoProcessing(video.getContentType());
    StandaloneVideoShow videoMetadata =
        videoRepo.save(
            new StandaloneVideoShow(
                video.getOriginalFilename(), video.getContentType(), video.getSize()));
    File[] hlsFiles = splitVideoToHlsChunks(videoMetadata.getId(), video.getInputStream());
    for (File hlsFile : hlsFiles) {
      String s3Key = S3_FOLDER + "/" + videoMetadata.getId() + "/standalone/" + hlsFile.getName();
      uploadToS3(s3Key, hlsFile);
    }
    return videoMetadata;
  }

  /**
   * Upload trailer to s3 storage.
   *
   * @param trailer video multipart file from the form
   * @throws IllegalArgumentException when multipart file is not a trailer
   * @throws ParseException when failed to parse trailer to HLS chunks with ffmpeg
   * @throws IOException when error allocating space to new trailer file
   * @throws S3Exception when error uploading file to S3
   * @return trailer object with metadata saved in database
   */
  public Trailer uploadTrailer(MultipartFile video) throws ParseException, IOException {
    BinaryContentUtils.assureVideoProcessing(video.getContentType());
    Trailer trailerMetadata =
        new Trailer(video.getOriginalFilename(), video.getContentType(), video.getSize());
    File[] hlsFiles = splitVideoToHlsChunks(trailerMetadata.getId(), video.getInputStream());
    for (File hlsFile : hlsFiles) {
      String s3Key = S3_FOLDER + "/" + trailerMetadata.getId() + "/trailer/" + hlsFile.getName();
      uploadToS3(s3Key, hlsFile);
    }
    return trailerMetadata;
  }

  /**
   * Upload an episode to s3 storage.
   *
   * @param episode video multipart file from the form
   * @param season number of the season
   * @param episode number of the episode
   * @throws IllegalArgumentException when multipart file is not a an episode
   * @throws ParseException when failed to parse an episode to HLS chunks with ffmpeg
   * @throws IOException when error allocating space to new episode file
   * @throws S3Exception when error uploading file to S3
   * @return episode object with metadata saved in database
   */
  public Episode uploadEpisode(
      MultipartFile video, String contentMetadataId, int season, int episode)
      throws ParseException, IOException {
    BinaryContentUtils.assureVideoProcessing(video.getContentType());
    var episodeMetadata =
        new Episode(
            video.getOriginalFilename(), video.getContentType(), season, episode, video.getSize());
    File[] hlsFiles = splitVideoToHlsChunks(episodeMetadata.getId(), video.getInputStream());
    for (File hlsFile : hlsFiles) {
      String s3Key =
          "%s/tv-series/%s/%d/%d/%s"
              .formatted(S3_FOLDER, contentMetadataId, season, episode, hlsFile.getName());
      uploadToS3(s3Key, hlsFile);
    }
    return episodeMetadata;
  }

  // TODO convert to hls with stream instead of temp folder
  /**
   * Split video into hls chunks and upload them to s3 storage.
   *
   * @param id video id from database
   * @param inputStream input stream of video file from database
   * @return array of hls chunk files
   * @throws ParseException when error parsing video file
   * @throws IOException when error reading or writing to file system (possible reason is not enough
   *     disk space)
   */
  private File[] splitVideoToHlsChunks(String id, InputStream inputStream)
      throws ParseException, IOException {
    String tempFolderPath = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID();
    File tempFolder = new File(tempFolderPath);
    tempFolder.mkdirs();

    // TODO determine video extention instead of using hardcoded one
    Path tempFile = Files.createFile(Path.of(tempFolderPath + "/" + id));
    Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

    String ffmpegCommand =
        "ffmpeg -i %s -profile:v baseline -level 3.0 -s 640x360 -start_number 0 -hls_time 10 -hls_list_size 0 -f hls %s/index.m3u8"
            .formatted(tempFile.toFile().getAbsolutePath(), tempFolder.getAbsolutePath());

    Process process = Runtime.getRuntime().exec(ffmpegCommand);
    try (var stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
      String line;
      while ((line = stdError.readLine()) != null) {
        LOG.error(line);
      }
    }

    String errmes = "Ошибка обработки видео при разбиение на части HLS.";
    int exitCode;
    try {
      exitCode = process.waitFor();
    } catch (InterruptedException e) {
      throw new ParseException(errmes);
    }
    if (exitCode != 0) {
      throw new ParseException(errmes);
    }

    return tempFolder.listFiles();
  }

  /**
   * Upload file to S3 bucket.
   *
   * @param s3Key S3 key for the uploaded file.
   * @param file file to be uploaded.
   */
  private void uploadToS3(String s3Key, File file) {
    try (InputStream inputStream = new FileInputStream(file)) {
      var putObjectRequest =
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(s3Key)
              .contentType(Files.probeContentType(file.toPath()))
              .build();
      s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.length()));
    } catch (IOException e) {
      String errmes = "Failed to upload file to S3";
      throw S3Exception.builder().message(errmes).build();
    }
  }

  private void deleteFolder(File folder) {
    File[] files = folder.listFiles();
    if (files != null) {
      for (File file : files) {
        file.delete();
      }
    }
    folder.delete();
  }

  /**
   * Delete related content from local metadata db and also from S3 storage.
   *
   * @param unparsedIds a comma-separated string of content metadata IDs
   * @throws ParseIdException when of invalid number format defined by api
   * @throws S3Exception when image wasn't deleted
   */
  public void deleteByIds(String unparsedIds) {
    List<String> ids = Arrays.asList(unparsedIds.split(","));
    if (ids.isEmpty()) {
      throw new ParseIdException();
    }
    ids.forEach(videoRepo::deleteById);
    S3GeneralOperations.deleteItems(S3_FOLDER, ids);
  }
}
