package ru.storage.content;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentDetailsRepo extends MongoRepository<ContentDetails, String> {

    List<ContentDetails> findByOrderByCreatedAtDesc(Pageable pageable);
}
