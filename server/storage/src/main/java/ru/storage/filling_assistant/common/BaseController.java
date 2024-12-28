package ru.storage.filling_assistant.common;

import org.springframework.http.ResponseEntity;

import java.util.List;

public abstract class BaseController<T extends Nameable> {

    private final BaseService<T> service;

    protected BaseController(BaseService<T> service) {
        this.service = service;
    }

    public ResponseEntity<String> tryToSaveOneEntity(T entity) {
        if (entity.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(service.save(entity).getId());
    }


    public ResponseEntity<List<String>> tryToSaveListOfEntities(List<T> entities) {
        entities.removeIf(element -> element.getName().isBlank());
        try {
            return ResponseEntity.ok(service.saveNewEntities(entities).stream()
                    .map(Nameable::getId)
                    .toList());
        } catch (Exception e) {
            return ResponseEntity.ok(service.saveWithoutDuplicates(entities).stream()
                    .map(Nameable::getId)
                    .toList());
        }
    }
}
