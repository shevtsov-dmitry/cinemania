package ru.storage.person.filming_group;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmingGroupRepo extends MongoRepository<FilmingGroup, String> {
}
