package ru.storage.filling_assistant.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
*
*/
@NoRepositoryBean
public interface BaseRepo<T> extends MongoRepository<T, Long> {
    int deleteByName(String name);
    T findByName(String name);
}
