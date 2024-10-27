package ru.storage.objectstorage.poster;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

	@GetMapping("/")
	public ResponseEntity<Poster> getPoster() {
		return null;
	}

	// @GetMapping(value = "/get/recent/{amount}", produces =
	// MediaType.APPLICATION_JSON_VALUE)
	// public ResponseEntity<List<>> getRecentlySavedPosters(@PathVariable int
	// amount) {
	// return ResponseEntity.ok(service.getRecentlySavedPosters(amount));
	// }

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePosterById(@PathVariable String id) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
		return service.deleteById(id)
				? new ResponseEntity<>("poster image with id %s successfully deleted.".formatted(id), httpHeaders,
						HttpStatus.OK)
				: ResponseEntity.notFound().build();
	}

}
