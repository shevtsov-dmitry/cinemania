package ru.storage.content.common;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FastByteArrayOutputStream;

import net.coobird.thumbnailator.Thumbnailator;

public class BinaryContentUtils {

    private BinaryContentUtils() {}

    private static final Logger log = LoggerFactory.getLogger(BinaryContentUtils.class);

    /**
     * Util method which compresses the input image on poster standard.
     *
     * @param inputStream initial image byte stream
     * @return compressed image
     */
    public static InputStream compressImage(InputStream inputStream) {
        try {
            var outStream = new FastByteArrayOutputStream();
            Thumbnailator.createThumbnail(inputStream, outStream, 500, 370);
            return outStream.getInputStream();
        } catch (IOException e) {
            log.error("Ошибка при сжатии файла изображения. Вызвана: {}", e.getMessage());
            return inputStream;
        }
    }
    
    /**
     * Assure an image processing by comparing input content type with expected.
     *
     * @param contentType contentType
     * @throws IllegalArgumentException when non image content used
     */
    public static void assureImageProcessing(String contentType) throws IllegalArgumentException {
        if (contentType == null || !contentType.startsWith("image")) {
            String errmes = "Ошибка при сохранении постера. Файл не является изображением. Был выбран файл типа " + contentType;
            log.warn(errmes);
            throw new IllegalArgumentException(errmes);
        }
    }

     /**
     * Assure a video processing by comparing input content type with expected.
     *
     * @param contentType contentType
     * @throws IllegalArgumentException when non image content used
     */
    public static void assureVideoProcessing(String contentType) throws IllegalArgumentException {
        if (contentType == null || !contentType.startsWith("video")) {
            String errmes = "Ошибка при сохранении видео. Файл не является видеороликом. Был выбран файл типа " + contentType;
            log.warn(errmes);
            throw new IllegalArgumentException(errmes);
        }
    }

}
