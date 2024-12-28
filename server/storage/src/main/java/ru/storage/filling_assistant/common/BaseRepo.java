package ru.storage.filling_assistant.common;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepo<T> extends MongoRepository<T, Long> {
    int deleteByName(String name);

    T findByName(String name);
}
