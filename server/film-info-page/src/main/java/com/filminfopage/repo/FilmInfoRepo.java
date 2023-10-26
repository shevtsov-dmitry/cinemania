package com.filminfopage.repo;

import com.filminfopage.model.FilmInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmInfoRepo extends JpaRepository<FilmInfo, Long> {

}
