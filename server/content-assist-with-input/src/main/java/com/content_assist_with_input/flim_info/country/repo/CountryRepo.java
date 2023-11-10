package com.content_assist_with_input.flim_info.country.repo;

import com.content_assist_with_input.flim_info.country.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepo extends JpaRepository<Country, Long> {

}
