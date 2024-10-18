package ru.storage.objectstorage.poster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.storage.metadata.ContentMetadata;
import ru.storage.metadata.MetadataRepo;

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

    public String save(MultipartFile file) {
        // TODO implement S3 storage
        return null;

    }

    public boolean deleteById(String id) {
        // TODO implement S3 storage
        return false;
    }

    // public List<ContentMetadata> queryMetadataRepoForIds(int amount) {
    // final Pageable requestedAmountRestriction = PageRequest.of(0, amount);
    // return metadataRepo.findRecentlyAdded(requestedAmountRestriction).stream()
    // .map(ContentMetadata::getPosterId)
    // .toList();
    // }

    // public List<Map<String, byte[]>> getRecentlySavedPosters(int amount) {
    // List<String> recentSavedPosterIds = queryMetadataRepoForIds(amount);
    // List<Map<String, byte[]>> imagesAndMetadata = new ArrayList<>(amount);
    //
    // for (String id : recentSavedPosterIds) {
    // Map<String, byte[]> data = new HashMap<>();
    //
    // Poster poster = posterRepo.getPosterById(id);
    // if (poster == null) {
    // data.put("metadataId", "metadataId: NULL".getBytes());
    // data.put("title", "<Название>".getBytes());
    // data.put("country", "<Страна>".getBytes());
    // data.put("releaseDate", "<Дата релиза>".getBytes());
    // data.put("mainGenre", "<Основной жанр>".getBytes());
    // data.put("subGenres", "<Дополнительные жанры>".getBytes());
    // data.put("age", "0".getBytes());
    // data.put("rating", "0.00".getBytes());
    // data.put("videoId", "videoId: NULL".getBytes());
    // } else {
    // final ContentMetadata metadata = metadataRepo.getByPosterId(poster.getId());
    // data.put("metadataId", metadata.getId().getBytes());
    // data.put("title", metadata.getTitle().getBytes());
    // data.put("releaseDate", metadata.getReleaseDate().getBytes());
    // data.put("country", metadata.getCountry().getBytes());
    // data.put("mainGenre", metadata.getMainGenre().getBytes());
    // data.put("subGenres", metadata.getSubGenres().toString().replace("[",
    // "").replace("]", "").getBytes());
    // data.put("age", metadata.getAge().toString().getBytes());
    // data.put("rating", String.valueOf(metadata.getRating()).getBytes());
    // data.put("poster", poster.getImage().getData());
    // data.put("videoId", metadata.getVideoId().getBytes());
    // }
    // imagesAndMetadata.add(data);
    // }
    // return imagesAndMetadata;
    // }

}
