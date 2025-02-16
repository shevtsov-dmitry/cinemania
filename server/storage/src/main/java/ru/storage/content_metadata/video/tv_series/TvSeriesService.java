package ru.storage.content_metadata.video.tv_series;

import org.springframework.stereotype.Service;

@Service
public class TvSeriesService {

  private final TvSeriesRepo repo;

  public TvSeriesService(TvSeriesRepo repo) {
    this.repo = repo;
  }

  public TvSeries saveMetadata(TvSeries tvSeries) {
    return repo.save(tvSeries);
  }
  
}
