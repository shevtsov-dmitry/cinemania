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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	 * @param file       - multipart file of image type
	 * @return
	 */
	@PostMapping("/upload")
	public ResponseEntity<Poster> savePoster(@RequestParam Long metadataId, @RequestParam MultipartFile file) {
		HttpHeaders headers = new HttpHeaders();
		try {
			final Poster savedPopsterMetadata = service.savePoster(metadataId, file);
			return new ResponseEntity<>(savedPopsterMetadata, HttpStatus.OK);
		} catch (Exception e) {
			headers.set("message",
					URLEncoder.encode("Ошибка при сохранении постера для видео.", StandardCharsets.UTF_8));
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Provides poster images from S3 storage.
	 *
	 * @param contentMetadataIds - splitted ids by ',' separator.
	 *                           example: "4,2,592,101,10"
	 * @return List<byte[]> of matched images
	 */
	@GetMapping("/images/{contentMetadataId}")
	public ResponseEntity<List<byte[]>> getImagesByContentMetadataId(@PathVariable String contentMetadataId) {
		try {
			return ResponseEntity.ok(service.getImagesByContentMetadataId(contentMetadataId));
		} catch (Exception e) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("message", URLEncoder.encode("Ошибка при получении постеров.", StandardCharsets.UTF_8));
			return new ResponseEntity<>(Collections.EMPTY_LIST, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Deletes saved poster which matches requested ids from S3 and local db.
	 *
	 * @param contentMetadataIds - splitted ids by ',' separator.
	 *                           example: "4,2,592,101,10"
	 * @return HttpStatus OK or INTERNAL_SERVER_ERROR
	 */
	@DeleteMapping("/ids/{ids}")
	public ResponseEntity<Void> deletePostersByIds(@PathVariable String ids) {
		try {
			service.deleteByIds();
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("message", "Ошибка при удалении постеров по их идентификаторам.");
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
