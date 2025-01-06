package ru.storage.filling_assistants.country;

import org.springframework.stereotype.Repository;
import ru.storage.filling_assistants.base.BaseRepo;

@Repository
public interface CountryRepo extends BaseRepo<Country, String> {
    void deleteByName(String name);
}
