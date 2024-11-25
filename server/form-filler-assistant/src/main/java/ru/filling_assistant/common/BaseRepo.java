package ru.filling_assistant.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
*
*/
@NoRepositoryBean
public interface BaseRepo<T> extends JpaRepository<T, Long> {
    int deleteByName(String name);
    T findByName(String name);
}
