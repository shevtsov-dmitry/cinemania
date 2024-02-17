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

    public ResponseEntity<String> tryToSaveOneEntity(T entity) {
        if (entity.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            return ResponseEntity.ok(service.save(entity).getId().toString());
        } catch (Exception e) {
            return attemptToRemoveDuplicatesThenSave(new ArrayList<>(List.of(entity)));
        }
    }

    private ResponseEntity<String> attemptToRemoveDuplicatesThenSave(List<T> singletonEntityList) {
        try {
            return ResponseEntity.badRequest().body(service.saveWithoutDuplicates(singletonEntityList).getFirst().getId().toString());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Cannot save any, because all requested entities already exist in database.");
        }
    }

    public ResponseEntity<List<Long>> tryToSaveListOfEntities(List<T> entities) {
        entities.removeIf(element -> element.getName().isBlank());
        try {
            return ResponseEntity.ok(service.saveNewEntities(entities).stream()
                    .map(Nameable::getId)
                    .collect(Collectors.toList()));
        } catch (Exception e) {

            return ResponseEntity.ok(service.saveWithoutDuplicates(entities).stream()
                    .map(Nameable::getId)
                    .collect(Collectors.toList()));
        }
    }
}
