package ru.storage.userpic;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPicsRepo extends MongoRepository<UserPic, String> {
}
