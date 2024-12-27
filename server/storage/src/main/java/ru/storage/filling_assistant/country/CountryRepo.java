package ru.storage.filling_assistant.country;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.storage.filling_assistant.common.BaseRepo;

import java.util.List;

public interface CountryRepo extends BaseRepo<Country> {

}
