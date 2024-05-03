package ru.streaming.service;

import static ru.streaming.constants.ApplicationConstants.DOWNLOAD_CHUNK_SIZE;
import static ru.streaming.constants.ApplicationConstants.VIDEO_STORAGE_PATH;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import ru.streaming.controller.VideoController;

@Service
public class VideoService {
    private static final Logger LOG = LoggerFactory.getLogger(VideoController.class);

    public Mono<ResponseEntity<byte[]>> prepareContent(final String filename, final String range) {
        String filepath = "%s%s.mp4".formatted(VIDEO_STORAGE_PATH, filename);

        // TODO gather video bytes into OutputStream
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new File(filepath));
            grabber.start();

            long fileSize = grabber.getLengthInFrames();
            String[] ranges = range.split("-");
            long rangeStart = Long.parseLong(ranges[0].substring(6)) * (long) grabber.getVideoFrameRate();
            long rangeEnd = ranges.length > 1 ? Long.parseLong(ranges[1]) * (long) grabber.getVideoFrameRate()
                    : rangeStart + DOWNLOAD_CHUNK_SIZE * (long) grabber.getVideoFrameRate();
            rangeEnd = Math.min(rangeEnd, fileSize - 1);

            LOG.info("filepath %s".formatted(filepath));
            File tempFile = File.createTempFile("video-clip", ".mp4");
            LOG.info("tempFile size is: %d".formatted(tempFile.length()));
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(tempFile, grabber.getImageWidth(),
                    grabber.getImageHeight(), grabber.getAudioChannels());
            recorder.setVideoCodec(grabber.getVideoCodec());
            recorder.setFormat("mp4");
            recorder.setFrameRate(grabber.getVideoFrameRate());
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.start();
            grabber.setFrameNumber((int) rangeStart);
            Frame frame;
            long frameCount = 0;
            while ((frame = grabber.grabFrame()) != null && frameCount < rangeEnd - rangeStart + 1) {
                recorder.record(frame);
                frameCount++;
            }

            recorder.close();
            grabber.close();

            byte[] videoClip = Files.readAllBytes(tempFile.toPath());
            tempFile.delete();

            final String contentRange = "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize;
            HttpHeaders headers = composeHeaders(videoClip.length, contentRange);
            HttpStatus httpStatus = rangeEnd >= fileSize ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT;
            return Mono.just(response(httpStatus, headers, videoClip));
        } catch (IOException e) {
            e.printStackTrace();
            var response = ResponseEntity.internalServerError().body(e.getMessage().getBytes());
            return Mono.just(response);
        }
    }

    private ResponseEntity<byte[]> response(HttpStatus httpStatus, HttpHeaders headers, byte[] binaryVideoContent) {
        return ResponseEntity.status(httpStatus)
                .headers(headers)
                .body(binaryVideoContent);
    }

    private static HttpHeaders composeHeaders(long contentLength, String contentRange) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4"));
        headers.setContentLength(contentLength);
        headers.set("Accept-Ranges", "bytes");
        headers.set("Content-Range", contentRange);
        return headers;
    }

    public byte[] readByteRange(byte[] binaryContent, long start, long end) {
        byte[] result = new byte[(int) (end - start) + 1];
        System.arraycopy(binaryContent, (int) start, result, 0, (int) (end - start) + 1);
        return result;
    }

}
