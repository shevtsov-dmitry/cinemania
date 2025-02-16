package ru.storage.content_metadata.video.episode;

import org.springframework.stereotype.Service;

@Service
public class EpisodeService {

  private final EpisodeRepo repo;

  public EpisodeService(EpisodeRepo repo) {
    this.repo = repo;
  }

  public Episode saveMetadata(Episode episode) {
    return repo.save(episode);
  }
  
}
