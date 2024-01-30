package ru.content_assist_with_input.filling_assistant.common;

import org.springframework.data.repository.CrudRepository;

import java.util.*;

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
                return "Cannot save because already exists in database.";
            } else {
                repo.saveAll(receivedEntities);
                List<String> receivedEntitiesName = receivedEntities.stream().map(T::getName).toList();
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
            return parseStringAnswer(entityNamesWithoutDuplicates);
        }
    }

    private static String parseStringAnswer(List<String> receivedEntities) {
        StringJoiner sj = new StringJoiner(", ", "", ".");
        receivedEntities.forEach(sj::add);
        return "Successfully added new elements: ".concat(sj.toString());
    }
}
