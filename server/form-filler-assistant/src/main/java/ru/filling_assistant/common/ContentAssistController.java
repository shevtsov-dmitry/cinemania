package ru.filling_assistant.common;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ContentAssistController<T extends Nameable> {

    private final ContentAssistService<T> service;

    protected ContentAssistController(ContentAssistService<T> service) {
        this.service = service;
    }

    public ResponseEntity<String> tryToSaveOneEntity(T country) {
        final String countryName = country.getName();
        if (countryName.isBlank()) {
            return ResponseEntity.badRequest().body("incoming parameter data is empty.");
        }

        try {
            T saved = service.save(country);
            return ResponseEntity.ok(saved.toString());
        } catch (Exception e) {
            final List<T> singletonEntityList = new ArrayList<>(List.of(country));
            return ResponseEntity.ok(service.saveWithoutDuplicates(singletonEntityList).getFirst().getId().toString());
        }
    }

    public ResponseEntity<List<String>> tryToSaveListOfEntities(List<T> entities) {
        entities.removeIf(element -> element.getName().isBlank());
        try {
            return ResponseEntity.ok(service.saveNewEntities(entities).stream()
                    .map(entity -> entity.getId().toString())
                    .collect(Collectors.toList()));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.ok(service.saveWithoutDuplicates(entities).stream()
                    .map(entity -> entity.getId().toString())
                    .collect(Collectors.toList()));
        }
    }
}
