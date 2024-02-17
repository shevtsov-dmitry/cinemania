package ru.filling_assistant.common;

import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ref.SoftReference;
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
        List<T> allEntitiesInDB = repo.findAll();
        if (!allEntitiesInDB.isEmpty()) {
            return removeDupsAndSaveDeliveredGenres(receivedEntities, allEntitiesInDB);
        } else {
            return removeDupsAndSaveAll(receivedEntities);
        }
    }

    private List<T> removeDupsAndSaveDeliveredGenres(List<T> receivedEntities, List<T> allGenres) throws IllegalArgumentException {
        List<String> allEntityNamesInDB = new ArrayList<>(allGenres.stream().map(T::getName).toList());
        receivedEntities.removeIf(receivedEntity -> allEntityNamesInDB.contains(receivedEntity.getName()));
        if (receivedEntities.isEmpty()) {
            throw new IllegalArgumentException("Cannot save any, because all requested entities already exist in database.");
        }

        List<T> filteredEntities = new ArrayList<>(receivedEntities.size());
        Set<String> set = new HashSet<>(receivedEntities.size());
        for (T receivedEntity : receivedEntities) {
            if(!set.contains(receivedEntity.getName())) {
                filteredEntities.add(receivedEntity);
            }
            set.add(receivedEntity.getName());
        }
        return repo.saveAll(filteredEntities);
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

    public T save(T entity)  {
        return repo.save(entity);
    }

    public List<T> saveNewEntities(List<T> entities) {
        return repo.saveAll(entities);
    }
}
