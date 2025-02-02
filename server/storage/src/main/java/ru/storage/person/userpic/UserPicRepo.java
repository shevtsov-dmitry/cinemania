package ru.storage.person.userpic;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPicRepo extends MongoRepository<UserPic, String> {
}
