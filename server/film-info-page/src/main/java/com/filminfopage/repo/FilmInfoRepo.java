package com.filminfopage.repo;

import com.filminfopage.model.FilmInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FilmInfoRepo extends JpaRepository<FilmInfo, Long> {
    @Query("SELECT t FROM FilmInfo t ORDER BY t.id DESC")
    List<FilmInfo> getLastSaved();
}
