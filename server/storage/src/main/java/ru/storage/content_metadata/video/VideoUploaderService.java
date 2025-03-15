package ru.storage.content_metadata.video;

import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.content_metadata.video.episode.EpisodeRepo;
import ru.storage.content_metadata.video.standalone.StandaloneVideoShowRepo;
import ru.storage.content_metadata.video.trailer.TrailerRepo;
import ru.storage.utils.BinaryContentUtils;
import ru.storage.utils.S3GeneralOperations;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class VideoUploaderService {

    @Value("${custom.s3.BUCKET_NAME}")
    private String bucketName;

    private static final String S3_FOLDER = "videos";
    private static final Logger LOG = LoggerFactory.getLogger(VideoUploaderService.class);

    private final S3Client s3Client;
    private final StandaloneVideoShowRepo standaloneVideoShowRepo;
    private final TrailerRepo trailerRepo;
    private final EpisodeRepo episodeRepo;
    public static File tempFolder = null;

    public VideoUploaderService(
            S3Client s3Client,
            StandaloneVideoShowRepo standaloneVideoShowRepo,
            TrailerRepo trailerRepo,
            EpisodeRepo episodeRepo) {
        this.s3Client = s3Client;
        this.standaloneVideoShowRepo = standaloneVideoShowRepo;
        this.trailerRepo = trailerRepo;
        this.episodeRepo = episodeRepo;
    }

    /**
     * Upload video to s3 storage.
     *
     * @param id    saved video metadata id from mongodb
     * @param video video multipart file from the form
     * @throws IllegalArgumentException when multipart file is not a video
     * @throws ParseException           when failed to parse video to HLS chunks with ffmpeg
     * @throws IOException              when error allocating space to new video file
     * @throws S3Exception              when error uploading file to S3
     */
    public void uploadStandaloneVideoShow(String id, MultipartFile video)
            throws ParseException, IOException {
        BinaryContentUtils.assureVideoProcessing(video.getContentType());
        File[] hlsFiles = splitVideoToHlsChunks(id, video.getInputStream());
        for (File hlsFile : hlsFiles) {
            String s3Key = S3_FOLDER + "/standalone/" + id + "/" + hlsFile.getName();
            uploadToS3(s3Key, hlsFile);
        }
        deleteTempFolder();
    }


    /**
     * Upload trailer to s3 storage.
     *
     * @param id    saved video metadata id from mongodb
     * @param video video multipart file from the form
     * @throws IllegalArgumentException when multipart file is not a trailer
     * @throws ParseException           when failed to parse trailer to HLS chunks with ffmpeg
     * @throws IOException              when error allocating space to new trailer file
     * @throws S3Exception              when error uploading file to S3
     */
    public void uploadTrailer(String id, MultipartFile video) throws ParseException, IOException {
        BinaryContentUtils.assureVideoProcessing(video.getContentType());
        File[] hlsFiles = splitVideoToHlsChunks(id, video.getInputStream());
        for (File hlsFile : hlsFiles) {
            String s3Key = S3_FOLDER + "/trailer/" + id + "/" + hlsFile.getName();
            uploadToS3(s3Key, hlsFile);
        }
    }

    /**
     * Upload an episode to s3 storage.
     *
     * @param id         saved video metadata id from mongodb
     * @param video      video multipart file from the form
     * @param tvSeriesId id of the tv show
     * @param season     number of the season
     * @param episode    number of the Episode
     * @throws IllegalArgumentException when multipart file is not a an episode
     * @throws ParseException           when failed to parse an episode to HLS chunks with ffmpeg
     * @throws IOException              when error allocating space to new episode file
     * @throws S3Exception              when error uploading file to S3
     */
    public void uploadEpisode(
            String id, MultipartFile video, String tvSeriesId, int season, int episode)
            throws ParseException, IOException {
        BinaryContentUtils.assureVideoProcessing(video.getContentType());
        File[] hlsFiles = splitVideoToHlsChunks(id, video.getInputStream());
        for (File hlsFile : hlsFiles) {
            String s3Key =
                    S3_FOLDER + "/tv-series/%s/%d/%d/%s".formatted(tvSeriesId, season, episode, id);
            uploadToS3(s3Key, hlsFile);
        }
    }

    // TODO convert to hls with stream instead of temp folder

    /**
     * Split video into hls chunks and upload them to s3 storage.
     *
     * @param id          video id from database
     * @param inputStream input stream of video file from database
     * @return array of hls chunk files
     * @throws ParseException when error parsing video file
     * @throws IOException    when error reading or writing to file system (possible reason is not enough
     *                        disk space)
     */
    private File[] splitVideoToHlsChunks(String id, InputStream inputStream)
            throws ParseException, IOException {
        String tempFolderPath = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID();
        tempFolder = new File(tempFolderPath);
        tempFolder.mkdirs();

        // TODO: Determine video extension dynamically instead of hardcoding
        Path tempFile = Files.createFile(Path.of(tempFolderPath + "/" + id));
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

        String ffmpegCommand =
                "ffmpeg -i %s -c:v libx264 -c:a aac -profile:v baseline -level 3.0 -start_number 0 -hls_time 10 -hls_list_size 0 -f hls %s/index.m3u8"
                        .formatted(tempFile.toFile().getAbsolutePath(), tempFolder.getAbsolutePath());

        ffmpegCommand = "ffmpeg -i %s -map 0:v:0 -map 0:a:0 -map 0:v:0 -map 0:a:0 -var_stream_map \"v:0,a:0 v:1,a:1\" -c:v libx264 -crf 23 -preset medium -c:a aac -b:a 128k -hls_time 10 -hls_list_size 0 -b:v:0 2000k -hls_segment_filename \"v%v/output_%03d.ts\" v%v/output.m3u8"
                .formatted(tempFile.toFile().getAbsolutePath(), tempFolder.getAbsolutePath());

        Process process = Runtime.getRuntime().exec(ffmpegCommand);
        try (var stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = stdError.readLine()) != null) {
                LOG.error("FFmpeg error: {}", line); // Improved logging for debugging
            }
        }

        String errmes = "Ошибка обработки видео при разбиении на части HLS.";
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
     * @param file  file to be uploaded.
     * @throws S3Exception when error uploading to S3
     */
    private void uploadToS3(String s3Key, File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            String contentType;
            if (s3Key.endsWith(".m3u8")) {
                contentType = "application/x-mpegURL";
            } else if (s3Key.endsWith(".ts")) {
                contentType = "video/MP2T";
            } else {
                contentType = Files.probeContentType(file.toPath()); // Fallback for other files
            }
            var putObjectRequest =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .contentType(contentType)
                            .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.length()));
        } catch (IOException e) {
            String errmes = "Возникла ошибка при загрузке видео в облачное хранилище S3";
            throw S3Exception.builder().message(errmes).build();
        }
    }

    /**
     * Delete related content from local metadata db and also from S3 storage.
     *
     * @param ids required ids
     * @throws S3Exception when image wasn't deleted
     */
    public void deleteStandaloneVideoShowByIds(List<String> ids) {
        ids.forEach(standaloneVideoShowRepo::deleteById);
        S3GeneralOperations.deleteItems(S3_FOLDER + "/standalone", ids);
    }

    /**
     * Delete related content from local metadata db and also from S3 storage.
     *
     * @param ids required ids
     * @throws S3Exception when image wasn't deleted
     */
    public void deleteTrailerByIds(List<String> ids) {
        ids.forEach(trailerRepo::deleteById);
        S3GeneralOperations.deleteItems(S3_FOLDER + "/trailer", ids);
    }

    private void deleteTempFolder() {
        for (File file : tempFolder.listFiles()) {
            file.delete();
        }
        tempFolder.delete();
    }

    // public void deleteEpisodeByIds(String unparsedIds) {
    // }

}
