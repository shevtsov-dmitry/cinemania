package ru.content_assist_with_input.filling_assistant.common;

import org.springframework.data.repository.CrudRepository;

import java.util.*;

public class ContentAssistService<T extends Nameable> {
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

    private List<String> removeDupsAndSaveAll(List<T> receivedEntities){
        Set<String> noDups = new HashSet<>();
        for (T entity : receivedEntities) {
            if (!noDups.contains(entity.getName())) {
                repo.save(entity);
            }
            noDups.add(entity.getName());
        }
        return noDups.stream().toList();
    }

//    private static String parseStringAnswer(List<String> receivedEntities) {
//        StringJoiner sj = new StringJoiner(", ", "", ".");
//        receivedEntities.forEach(sj::add);
//        return "Successfully added new elements: ".concat(sj.toString());
//    }
}
