package com.content_assist_with_input.flim_info.country.repo;

import com.content_assist_with_input.flim_info.country.model.Country;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepo extends JpaRepository<Country, Long> {
    @Query("FROM Country c WHERE c.name LIKE ?1%")
    List<String> getCountryNamesBySimilarStringSequence(String sequence, Pageable pageable);
}
