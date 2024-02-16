package ru.filling_assistant.common;

import jakarta.persistence.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ContentAssistService<T extends Nameable> {
    private final JpaRepository<T, Long> repo;

    public ContentAssistService(JpaRepository<T, Long> repo) {
        this.repo = repo;
    }

    public List<T> saveWithoutDuplicates(List<T> receivedEntities) throws IllegalArgumentException {
        List<T> allGenres = repo.findAll();
        if (!allGenres.isEmpty()) {
            return removeDupsAndSaveDeliveredGenres(receivedEntities, allGenres);
        } else {
            return removeDupsAndSaveAll(receivedEntities);
        }
    }

    private List<T> removeDupsAndSaveDeliveredGenres(List<T> receivedEntities, List<T> allGenres) throws IllegalArgumentException {
        List<String> entitiesNames = allGenres.stream().map(T::getName).toList();
        receivedEntities.removeIf(entity -> entitiesNames.contains(entity.getName()));
        if (receivedEntities.isEmpty()) {
            throw new IllegalArgumentException("Cannot save any, because all requested genres already exist in database.");
        }
        return repo.saveAll(receivedEntities);
    }

    private List<T> removeDupsAndSaveAll(List<T> receivedEntities) {
        Set<String> noDups = new HashSet<>();
        List<T> savedEntities = new ArrayList<>(receivedEntities.size());
        for (T entity : receivedEntities) {
            if (!noDups.contains(entity.getName())) {
                savedEntities.add(repo.save(entity));
            }
            noDups.add(entity.getName());
        }
        // ? maybe should return no dups instead
        return savedEntities;
    }

    public T save(T entity) {
        return repo.save(entity);
    }

    public List<T> saveNewEntities(List<T> entities) {
        return repo.saveAll(entities);
    }
}
