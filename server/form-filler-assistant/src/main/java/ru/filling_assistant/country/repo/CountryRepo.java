package ru.filling_assistant.country.repo;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.filling_assistant.country.model.Country;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Calendar;
import java.util.List;

public interface CountryRepo extends JpaRepository<Country, Long> {
    @Query("FROM Country c WHERE c.name LIKE ?1%")
    List<String> getCountryNamesBySimilarStringSequence(String sequence, Pageable pageable);
}
