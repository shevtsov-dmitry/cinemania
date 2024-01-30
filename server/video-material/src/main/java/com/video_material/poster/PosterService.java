package com.video_material.poster;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class PosterService {

    private final PosterRepo repo;

    @Autowired
    public PosterService(PosterRepo repo) {
        this.repo = repo;
    }

    public ResponseEntity<Map<String, String>> save(MultipartFile file) throws IOException {
        int hash = Objects.requireNonNull(file.getOriginalFilename()).hashCode();
        if (hash == 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is not attached."));
        }
        Poster poster = new Poster();
        poster.setImage(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        poster = repo.insert(poster);
        return composeSuccessfulSaveAnswer(poster);
    }

    private static ResponseEntity<Map<String, String>> composeSuccessfulSaveAnswer(Poster poster) {
        Map<String, String> map = new HashMap<>();
        map.put("message", STR."saved in database with id: \{poster.getId()}");
        map.put("id", poster.getId());
        return ResponseEntity.ok().body(map);
    }

    public ResponseEntity<byte[]> getById(String id) {
        return composeAnswer(repo.getPosterById(id));
    }

    private static ResponseEntity<byte[]> composeAnswer(Poster poster) {
        if(poster == null) {
            return ResponseEntity.badRequest().body("Poster not found".getBytes());
        }
        byte[] imageBytes = poster.getImage().getData();
        return ResponseEntity.ok().body(imageBytes);
    }

    public ResponseEntity<String> deleteById(String id) {
        return repo.deletePosterById(id) > 0 ?
                ResponseEntity.ok().body(STR."video file with id \{id} successfully deleted.") :
                ResponseEntity.notFound().build();
    }

}
