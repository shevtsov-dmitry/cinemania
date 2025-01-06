package ru.storage.filling_assistants.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BaseController<T extends Nameable> {

    private final BaseService<T> baseService;

    public BaseController(BaseService<T> baseService) {
        this.baseService = baseService;
    }

    public ResponseEntity<Void> save(T entity) {
        baseService.save(entity);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public ResponseEntity<List<String>> getAll() {
        return ResponseEntity.ok(baseService.getAll());
    }

    public ResponseEntity<Void> deleteByName(String name) {
        baseService.deleteByName(name);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
