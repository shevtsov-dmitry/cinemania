package ru.content_assist_with_input.filling_assistant.common;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.content_assist_with_input.filling_assistant.genres.model.Genre;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ContentAssistController<T extends AbstractEntity> {
    private final Class<T> aClass;

    @Qualifier("contentAssistService")
    @Autowired
    private ContentAssistService<T> service;
    private final Gson gson = new Gson();

    public ContentAssistController(Class<T> aClass) {
        this.aClass = aClass;
    }

    public ResponseEntity<String> addOne(String name) {
        if (name.isBlank()) {
            return ResponseEntity.badRequest().body("incoming parameter data is empty.");
        }
        try {
            T entity = aClass.getDeclaredConstructor().newInstance();
            entity.setName(name);
            return saveOneAndGetId(entity);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Couldn't create instance of the class on server.");
        }
    }

    private ResponseEntity<String> saveOneAndGetId(T entity) {
        try {
            return ResponseEntity.ok(service.save(entity).toString());
        } catch (Exception e) {
            final List<T> extensibleMonoList = new ArrayList<>(List.of(entity));
            String json = gson.toJson(service.saveWithoutDuplicates(extensibleMonoList));
            return ResponseEntity.badRequest().body(json);
        }
    }

    public ResponseEntity<String> addMany(List<String> entityNamesList) {
        entityNamesList.removeIf(String::isBlank);
        List<T> entities = new ArrayList<>(entityNamesList.size());
        for (String name : entityNamesList) {
            try {
                T entity = aClass.getDeclaredConstructor().newInstance();
                entity.setName(name);
                entities.add(entity);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Couldn't create new instance on server side.");
            }
        }
        try {
            return ResponseEntity.ok(gson.toJson(service.saveNewEntities(entities)));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.ok(gson.toJson(service.saveWithoutDuplicates(entities)));
        }
    }

    public ResponseEntity<String> getBySequence(String sequence) {

    }

    public ResponseEntity<String> delete(List<String> entityNamesList) {
        return ResponseEntity.ok("");
    }
}
