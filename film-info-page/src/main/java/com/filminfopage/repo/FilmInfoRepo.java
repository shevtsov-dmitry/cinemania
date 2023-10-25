package com.filminfopage.repo;

import com.filminfopage.entity.FilmInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmInfoRepo extends JpaRepository<Long, FilmInfo> {

}
