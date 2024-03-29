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
    public List<Map<String, byte[]>> getRecentlySavedPosters(int amount) {
        List<String> recentSavedPosterIds = getRecentSavedPosterIds(amount);
        List<Map<String, byte[]>> imagesAndMetadata = new ArrayList<>(amount);

        for (String id : recentSavedPosterIds) {
            Map<String, byte[]> data = new HashMap<>();

            Poster poster = posterRepo.getPosterById(id);
            data.put("poster", poster.getImage().getData());

            final ContentMetadata metadata = metadataRepo.getByPosterId(poster.getId());
            data.put("metadataId", metadata.getId().getBytes());
            data.put("title", metadata.getTitle().getBytes());
            data.put("genre", metadata.getGenre().getBytes());
            data.put("releaseDate", metadata.getReleaseDate().getBytes());
            data.put("rating", String.valueOf(metadata.getRating()).getBytes());
            data.put("country", metadata.getCountry().getBytes());
            data.put("age", metadata.getAge().toString().getBytes());
            data.put("videoId", metadata.getVideoId().getBytes());

            imagesAndMetadata.add(data);
        }
        return imagesAndMetadata;
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
