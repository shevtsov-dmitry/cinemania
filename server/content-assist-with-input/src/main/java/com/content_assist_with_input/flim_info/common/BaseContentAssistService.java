package com.content_assist_with_input.flim_info.common;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseContentAssistService<T extends Nameable> {
    private final CrudRepository<T, Long> repo;

    public BaseContentAssistService(CrudRepository<T, Long> repo) {
        this.repo = repo;
    }

    public String saveWithoutDuplicates(List<T> receivedEntities) {
        List<T> entities = (List<T>) repo.findAll();
        if (!entities.isEmpty()) {
            List<String> entitiesNames = entities.stream().map(T::getName).toList();
            receivedEntities.removeIf(entity -> entitiesNames.contains(entity.getName()));
            if (receivedEntities.isEmpty()) {
                return "Cannot save because already exist in database.";
            } else {
                repo.saveAll(receivedEntities);
                List<String> receivedEntitiesName = receivedEntities.stream().map(T::getName).toList();
                // parse string answer
                return parseStringAnswer(receivedEntitiesName);
            }
        } else {
            Map<String, T> nameToEntity = new HashMap<>(receivedEntities.size());
            for (T entity : receivedEntities) {
                nameToEntity.put(entity.getName(), entity);
            }
            List<String> entityNamesWithoutDuplicates = new ArrayList<>(nameToEntity.size());
            for (String name : nameToEntity.keySet()) {
                entityNamesWithoutDuplicates.add(name);
                repo.save(nameToEntity.get(name));
            }
            // parse string answer
            return parseStringAnswer(entityNamesWithoutDuplicates);
        }
    }

    private static String parseStringAnswer(List<String> receivedEntities) {
        StringBuilder builder = new StringBuilder();
        builder.append("Successfully added new elements: ");
        for (String entity : receivedEntities) {
            builder.append(entity).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);
        builder.append(".");
        return builder.toString();
    }
}
