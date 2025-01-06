package ru.storage.filling_assistants.base;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NoRepositoryBean
@Component
public interface BaseRepo<T extends Nameable, ID> extends MongoRepository<T, ID> {

    void deleteByName(String name);
}
