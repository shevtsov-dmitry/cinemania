package ru.storage.content_metadata.video.trailer;

import org.springframework.stereotype.Service;

@Service
public class TrailerService {

    private final TrailerRepo repo;

    public TrailerService(TrailerRepo repo) {
        this.repo = repo;
    }

    public Trailer saveMetadata(Trailer trailer) {
        return repo.save(trailer);
    }

    public void deleteMetadata(String id) {
        repo.deleteById(id);
    }

}
