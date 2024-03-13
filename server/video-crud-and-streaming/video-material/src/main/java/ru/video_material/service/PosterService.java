package ru.video_material.service;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.ContentMetadata;
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
                poster.getImage().getData());
    }

    public boolean deleteById(String id) {
        return posterRepo.deletePosterById(id) > 0;
    }

    public List<String> getRecentSavedPosterIds(int amount) {
        Pageable requestedAmountRestriction = PageRequest.of(0, amount);
        return metadataRepo.findTopNByOrderByCreatedAtDesc(requestedAmountRestriction).stream()
                .map(ContentMetadata::getPosterId)
                .toList();
    }


    /* *
    * list of byte[2]
    * byte[0] - metadata id (mutual, not poster)
    * byte[1] - poster binary image content
    * */
    public List<List<byte[]>> getRecentlySavedPosters(int amount) {
        List<String> recentSavedPosterIds = getRecentSavedPosterIds(amount);
        List<List<byte[]>> images = new ArrayList<>(amount);
        for (String id : recentSavedPosterIds) {
            List<byte[]> metadataIdAndPosterBinary = new ArrayList<>(2);
            Poster poster = posterRepo.getPosterById(id);
            String metadataId = metadataRepo.getByPosterId(poster.getId()).getId();
            metadataIdAndPosterBinary.add(metadataId.getBytes());
            metadataIdAndPosterBinary.add(poster.getImage().getData());
            images.add(metadataIdAndPosterBinary);
        }
        return images;
    }

    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(String title) {
        List<ContentMetadata> occurrences = metadataRepo.getByTitle(title);
        if (occurrences == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(occurrences);
    }

    public ResponseEntity<ContentMetadata> getMetadataById(String id) {
        ContentMetadata metadata = metadataRepo.getById(id);
        if (metadata == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(metadata);
    }

}
