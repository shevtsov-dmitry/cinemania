package ru.storage.objectstorage.poster;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static ru.storage.utility.HttpHeaderHelpers.writeMessageHeader;

@RestController
@RequestMapping("/api/v1/posters")
public class PosterController {

	private final PosterService service;

	public PosterController(PosterService service) {
		this.service = service;
	}

	/**
	 * Saves poster into S3 and its metadata into local db
	 *
	 * @param metadataId - id of existing video content metadata
	 * @param image      - multipart file of image type
	 * @return
	 */
	@PostMapping("/upload")
	public ResponseEntity<Poster> savePoster(@RequestParam Long metadataId, @RequestParam MultipartFile image) {
		HttpHeaders headers = new HttpHeaders();
		if (!image.getContentType().startsWith("image")) {
			writeMessageHeader(headers, "Ошибка при сохранении постера. Файл не является изображением.");
			return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
		}

		try {
			final Poster savedPopsterMetadata = service.savePoster(metadataId, image);
			return new ResponseEntity<>(savedPopsterMetadata, HttpStatus.OK);
		} catch (Exception e) {
			writeMessageHeader(headers, "Ошибка при сохранении постера для видео.");
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Provides poster images from S3 storage.
	 *
	 * @param contentMetadataIds - splitted ids by ',' separator.
	 *                           Also supports single id instance.
	 *                           example: "4,2,592,101,10"
	 * @return List<byte[]> of matched images
	 */
	@GetMapping("/images/{contentMetadataIds}")
	public ResponseEntity<List<byte[]>> getImagesByContentMetadataId(@PathVariable String contentMetadataIds) {
		try {
			return ResponseEntity.ok(service.getImagesByContentMetadataId(contentMetadataIds));
		} catch (Exception e) {
			HttpHeaders headers = new HttpHeaders();
			writeMessageHeader(headers, "Ошибка при получении постеров.");
			return new ResponseEntity<>(Collections.EMPTY_LIST, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Replaces existing poster with a new one.
	 *
	 * @param metadataId - content metadata id
	 * @param image      - multipart file of image type
	 */
	@PutMapping("/change")
	public ResponseEntity<Void> updateExistingImage(@RequestParam Long metadataId, @RequestParam MultipartFile image) {
		HttpHeaders headers = new HttpHeaders();
		if (!image.getContentType().startsWith("image")) {
			writeMessageHeader(headers, "Постер успешно заменён на новый.");
			return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
		}

		try {
			service.updateExistingImage(metadataId, image);
			writeMessageHeader(headers, "Постер успешно заменён на новый.");
			return new ResponseEntity<>(null, headers, HttpStatus.OK);
		} catch (Exception e) {
			writeMessageHeader(headers, "Неудалось заменить существующий постер.");
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Deletes saved poster which matches requested ids from S3 and local db.
	 *
	 * @param contentMetadataIds - splitted ids by ',' separator.
	 *                           Also supports single id instance.
	 *                           example: "4,2,592,101,10"
	 * @return HttpStatus OK or INTERNAL_SERVER_ERROR
	 */
	@DeleteMapping("/ids/{contentMetadataIds}")
	public ResponseEntity<Void> deletePostersByIds(@PathVariable String contentMetadataIds) {
		HttpHeaders headers = new HttpHeaders();
		try {
			service.deleteByIds(contentMetadataIds);
			writeMessageHeader(headers, "Выбранные постеры успешно удалены");
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			writeMessageHeader(headers, "Ошибка при удалении постеров по их идентификаторам.");
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
