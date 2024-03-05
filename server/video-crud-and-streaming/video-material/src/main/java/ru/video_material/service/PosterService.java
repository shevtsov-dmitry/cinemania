package ru.video_material.service;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.Poster;
import ru.video_material.repo.MetadataRepo;
import ru.video_material.repo.PosterRepo;
import ru.video_material.util.PosterWithMetadata;

import java.io.IOException;
import java.util.*;

@Service
public class PosterService {

    private final PosterRepo posterRepo;
    private final MetadataRepo metadataRepo;

    @Autowired
    public PosterService(PosterRepo posterRepo, MetadataRepo metadataRepo) {
        this.posterRepo = posterRepo;
        this.metadataRepo = metadataRepo;
    }

    public String save(MultipartFile file) throws IOException, NullPointerException, IllegalArgumentException {
        Poster poster = new Poster();
        int hash = Objects.requireNonNull(file.getOriginalFilename()).hashCode();
        if (hash == 0) {
            throw new IllegalArgumentException();
        }
        poster.setImage(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        poster = posterRepo.insert(poster);
        return poster.getId();
    }


    public PosterWithMetadata getPosterWithMetadataById(final String id) {
        Poster poster = posterRepo.getPosterById(id);
        if (poster == null) {
            throw new NullPointerException();
        }
        return new PosterWithMetadata(
                metadataRepo.getByPosterId(poster.getId()).getId(),
                poster.getImage().getData()
        );
    }

    public boolean deleteById(String id) {
        return posterRepo.deletePosterById(id) > 0;
    }


}