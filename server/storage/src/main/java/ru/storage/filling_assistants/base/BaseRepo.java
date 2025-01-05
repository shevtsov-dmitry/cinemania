package ru.storage.filling_assistants.base;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BaseRepo<T, ID> extends MongoRepository<T, ID> {
}
