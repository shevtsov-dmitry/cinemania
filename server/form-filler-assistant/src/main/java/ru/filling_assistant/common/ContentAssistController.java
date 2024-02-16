package ru.filling_assistant.common;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

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
            final List<T> oneContryList = new ArrayList<>(List.of(country));
            return ResponseEntity.ok(service.saveWithoutDuplicates(oneContryList).getFirst().toString());
        }
    }

    public ResponseEntity<List<T>> tryToSaveListOfEntities(List<T> entities) {
        entities.removeIf(country -> country.getName().isBlank());
        try {
            return ResponseEntity.ok(service.saveNewEntities(entities));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.ok(service.saveWithoutDuplicates(entities));
        }
    }
}
