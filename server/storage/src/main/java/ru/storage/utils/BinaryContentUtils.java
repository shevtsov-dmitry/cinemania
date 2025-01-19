package ru.storage.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.coobird.thumbnailator.Thumbnailator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FastByteArrayOutputStream;

/**
 * A utility class for general operations with binary content. Content is stored in S3 object
 * storage.
 */
public class BinaryContentUtils {

  private BinaryContentUtils() {}

  public static final String DEFAULT_DELIMER = "%%SPLIT_DELIMITER%%";
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
      String errmes =
          "Ошибка при сохранении постера. Файл не является изображением. Был выбран файл типа "
              + contentType;
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
      String errmes =
          "Ошибка при сохранении видео. Файл не является видеороликом. Был выбран файл типа "
              + contentType;
      log.warn(errmes);
      throw new IllegalArgumentException(errmes);
    }
  }

  public static byte[] combineWithDelimiter(List<byte[]> dataList, byte[] delimiter) {
    int totalLength =
        dataList.stream().mapToInt(arr -> arr.length).sum()
            + delimiter.length * (dataList.size() - 1);
    byte[] result = new byte[totalLength];
    int offset = 0;

    for (int i = 0; i < dataList.size(); i++) {
      byte[] data = dataList.get(i);
      System.arraycopy(data, 0, result, offset, data.length);
      offset += data.length;

      if (i < dataList.size() - 1) {
        System.arraycopy(delimiter, 0, result, offset, delimiter.length);
        offset += delimiter.length;
      }
    }

    return result;
  }

  /**
   * Concatenates a list of byte arrays with a default delimiter. The default delimiter is a this
   * class static field {@link #DEFAULT_DELIMITER}. The current value of the default delimiter is
   * "{@value #DEFAULT_DELIMITER}".
   *
   * @param dataList binary content list
   * @return single byte array containing the concatenated data with the delimiter in between each pair of bytes.
   */
  public static byte[] combineWithDefaultDelimiter(List<byte[]> dataList) {
    return combineWithDelimiter(dataList, DEFAULT_DELIMER.getBytes());
  }


}
