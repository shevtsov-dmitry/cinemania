package ru.storage.content_metadata.video.standalone;

import org.springframework.stereotype.Service;

@Service
public class StandaloneVideoShowService {
  
  private final StandaloneVideoShowRepo repo;

  public StandaloneVideoShowService(StandaloneVideoShowRepo repo) {
    this.repo = repo;
  }

  public StandaloneVideoShow saveMetadata(StandaloneVideoShow show) {
    return repo.save(show);
  }

  public void deleteMetadata(String id) {
    repo.deleteById(id);
  }
}
