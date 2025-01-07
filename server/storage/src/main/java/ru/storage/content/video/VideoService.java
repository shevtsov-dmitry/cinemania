package ru.storage.content.video;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.storage.content.common.S3GeneralOperations;
import ru.storage.content.exceptions.ParseRequestIdException;
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
    private final S3GeneralOperations s3GeneralOperations;

    public VideoService(S3Client s3Client, VideoRepo videoRepo, S3GeneralOperations s3GeneralOperations) {
        this.s3Client = s3Client;
        this.videoRepo = videoRepo;
        this.s3GeneralOperations = s3GeneralOperations;
    }

    /**
     * Assure a video processing by comparing input content type with expected.
     *
     * @param contentType contentType
     * @throws IllegalArgumentException when non image content used
     */
    private void assureVideoProcessing(String contentType) throws IllegalArgumentException {
        if (contentType == null || !contentType.startsWith("video")) {
            String errmes = "Ошибка при сохранении видео. Файл не является видеороликом. Был выбран файл типа " + contentType;
            LOG.warn(errmes);
            throw new IllegalArgumentException(errmes);
        }
    }

    // TODO create upload method by hls chunks

    /**
     * Save video metadata to a database.
     *
     * @param videoMetadata video object metadata
     * @throws IllegalArgumentException when content type if not an image
     */
    public VideoMetadata saveMetadata(VideoMetadata videoMetadata) {
        if (videoMetadata == null) {
            LOG.warn("Error saving video object from request, because it is null.");
            throw new IllegalArgumentException("Метаданные видеофайла отсутствуют.");
        }
        assureVideoProcessing(videoMetadata.getContentType());
        return videoRepo.save(videoMetadata);
    }

    /**
     * Upload video to s3 storage.
     *
     * @param id    id
     * @param video video multipart file from the form
     * @throws IllegalArgumentException when multipart file is not a video
     * @throws S3Exception              when error uploading file to S3
     */
     public void uploadVideo(String id, MultipartFile video) {
         assureVideoProcessing(video.getContentType());

         // Temporary folder for HLS files
         String tempDir = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID();
         File tempFolder = new File(tempDir);
         tempFolder.mkdirs();

         try {
             // TODO determine video extention instead of using hardcoded one
             Path tempFile = Files.createFile(Path.of(tempDir + "/" + id));
             Files.copy(video.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

             convertToHLS(tempFile.toFile(), tempFolder);

             // Upload HLS files to S3
             File[] hlsFiles = tempFolder.listFiles();
             System.out.println(Arrays.toString(tempFolder.list()));
             if (hlsFiles != null) {
                 for (File hlsFile : hlsFiles) {
                     uploadToS3(S3_FOLDER + "/" + id + "/" + hlsFile.getName(), hlsFile);
                 }
             }

             tempFile.toFile().delete();
             deleteFolder(tempFolder);

         } catch (IOException | RuntimeException e) {
             String errmes = "Error processing video.";
             e.printStackTrace();
             LOG.warn("{}. {}", errmes, e.getMessage());
             throw S3Exception.builder().message(errmes).build();
         }
     }

     // Method to convert video to HLS format
     private void convertToHLS(File inputFile, File outputDir) throws IOException {
         String ffmpegCommand = String.format(
             "ffmpeg -i %s -profile:v baseline -level 3.0 -s 640x360 -start_number 0 -hls_time 10 -hls_list_size 0 -f hls %s/index.m3u8",
             inputFile.getAbsolutePath(),
             outputDir.getAbsolutePath()
         );

         Process process = Runtime.getRuntime().exec(ffmpegCommand);

         // Wait for the process to complete
         try (var stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
             String line;
             while ((line = stdError.readLine()) != null) {
                 LOG.error(line);
             }
         }

         int exitCode;
		try {
			exitCode = process.waitFor();
		} catch (InterruptedException e) {
		      throw new RuntimeException("FFmpeg process failed");
		}
         if (exitCode != 0) {
             throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
         }
     }

     // Helper method to upload files to S3
     private void uploadToS3(String s3Key, File file) {
         try (InputStream inputStream = new FileInputStream(file)) {
             var putObjectRequest = PutObjectRequest.builder()
                 .bucket(bucketName)
                 .key(s3Key)
                 .contentType(Files.probeContentType(file.toPath()))
                 .build();
             s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.length()));
         } catch (IOException e) {
             throw new RuntimeException("Failed to upload file to S3", e);
         }
     }

     // Helper method to delete temporary folders
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
     * @throws ParseRequestIdException when of invalid number format defined by api
     * @throws S3Exception             when image wasn't deleted
     */
    public void deleteByIds(String unparsedIds) {
        List<String> ids = Arrays.asList(unparsedIds.split(","));
        if (ids.isEmpty()) {
            throw new ParseRequestIdException();
        }
        ids.forEach(videoRepo::deleteById);
        s3GeneralOperations.deleteFromS3(S3_FOLDER, ids);
    }

}
