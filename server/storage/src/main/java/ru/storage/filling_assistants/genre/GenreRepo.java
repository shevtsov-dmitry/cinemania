package ru.storage.filling_assistants.genre;

import org.springframework.stereotype.Repository;
import ru.storage.filling_assistants.base.BaseRepo;

@Repository
public interface GenreRepo extends BaseRepo<Genre, String> {
}
