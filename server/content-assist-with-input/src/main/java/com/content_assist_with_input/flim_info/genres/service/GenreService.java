package com.content_assist_with_input.flim_info.genres.service;

import com.content_assist_with_input.flim_info.common.BaseContentAssistService;
import com.content_assist_with_input.flim_info.genres.model.Genre;
import com.content_assist_with_input.flim_info.genres.repo.GenreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService extends BaseContentAssistService<Genre> {

    private final GenreRepo repo;
    private final Pageable foundGenreAmountRestriction = PageRequest.of(0,5);

    @Autowired
    public GenreService(GenreRepo repo) {
        super(repo);
        this.repo = repo;
    }

    public List<String> findMatchedGenres(String sequence) {
        return repo.getGenresNamesBySimilarStringSequence(sequence, foundGenreAmountRestriction);
    }

    @Override
    public String saveWithoutDuplicates(List<Genre> receivedEntities) {
        return super.saveWithoutDuplicates(receivedEntities);
    }
}