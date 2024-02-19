package ru.filling_assistant.country;

import ru.filling_assistant.common.BaseRepo;
import ru.filling_assistant.country.Country;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepo extends BaseRepo<Country> {
    @Query("SELECT c.name FROM Country c WHERE c.name LIKE ?1%")
    List<String> getCountryNamesBySimilarStringSequence(String sequence, Pageable pageable);
}
