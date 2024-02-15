package ru.filling_assistant.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContentAssistService<T extends AbstractNameableEntity> {
    private final CrudRepository<T, Long> repo;

    public ContentAssistService(CrudRepository<T, Long> repo) {
        this.repo = repo;
    }

    public List<String> saveWithoutDuplicates(List<T> receivedEntities) throws IllegalArgumentException {
        List<T> allGenres = (List<T>) repo.findAll();
        if (!allGenres.isEmpty()) {
            return removeDupsAndSaveDeliveredGenres(receivedEntities, allGenres);
        } else {
            return removeDupsAndSaveAll(receivedEntities);
        }
    }

    private List<String> removeDupsAndSaveDeliveredGenres(List<T> receivedEntities, List<T> allGenres) throws IllegalArgumentException {
        List<String> entitiesNames = allGenres.stream().map(T::getName).toList();
        receivedEntities.removeIf(entity -> entitiesNames.contains(entity.getName()));
        if (receivedEntities.isEmpty()) {
            throw new IllegalArgumentException("Cannot save any, because all requested genres already exist in database.");
        }
        repo.saveAll(receivedEntities);
        return receivedEntities.stream().map(T::getName).toList();
    }

    private List<String> removeDupsAndSaveAll(List<T> receivedEntities) {
        Set<String> noDups = new HashSet<>();
        for (T entity : receivedEntities) {
            if (!noDups.contains(entity.getName())) {
                repo.save(entity);
            }
            noDups.add(entity.getName());
        }
        return noDups.stream().toList();
    }

    public T save(T entity) {
        return repo.save(entity);
    }

    public List<String> saveNewEntities(List<T> entities) throws UnsupportedOperationException, DataIntegrityViolationException {
        List<String> saved = new ArrayList<>();
        repo.saveAll(entities)
                .forEach(el -> saved.add(el.getName()));
        return saved;
    }
}
