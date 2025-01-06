package ru.storage.filling_assistants.base;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BaseService<T extends Nameable> {

    private final BaseRepo<T, String> baseRepo;

    public BaseService(BaseRepo<T, String> baseRepo) {
        this.baseRepo = baseRepo;
    }

    void save(T entity) {
        baseRepo.save(entity);
    }

    List<String> getAll() {
        return baseRepo.findAll().stream()
                .map(T::getName)
                .toList();
    }

    void deleteByName(String name) {
        baseRepo.deleteByName(name);
    }

}
