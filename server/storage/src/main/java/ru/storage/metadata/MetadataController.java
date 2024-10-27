package ru.storage.metadata;

import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/videos/metadata")
public class MetadataController {

	private final MetadataService service;

	public MetadataController(MetadataService service) {
		this.service = service;
	}

	@PostMapping("/save")
	public ResponseEntity<ContentMetadata> saveMetadata(@RequestBody ContentMetadata contentMetadata) {
		Optional<ContentMetadata> content = service.saveMetadata(contentMetadata);
		return content.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.internalServerError().build());
	}

	@GetMapping(value = "/get/metadata/byTitle/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(@PathVariable String title) {
		return service.getMetadataByTitle(title);
	}

	@GetMapping(value = "/get/metadata/byId/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ContentMetadata> getMetadataById(@PathVariable Long id) {
		return service.getMetadataById(id);
	}

	@DeleteMapping("/delete/metadata/byId/{id}")
	public ResponseEntity<String> deleteMetadataById(@PathVariable Long id) {
		return service.deleteMetadataById(id);
	}

}
