package com.filminfopage.service;

import com.filminfopage.model.Poster;
import com.filminfopage.repo.PosterRepo;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
public class PosterService {

    private final PosterRepo repo;

    @Autowired
    public PosterService(PosterRepo repo) {
        this.repo = repo;
    }

    public ResponseEntity<String> save(String title, MultipartFile file) throws IOException {
        if (title.isBlank()) {
            return ResponseEntity.badRequest().body("Title is not specified.");
        }
        int hash = Objects.requireNonNull(file.getOriginalFilename()).hashCode();
        if (hash == 0) {
            return ResponseEntity.badRequest().body("File is not attached.");
        }
        Poster poster = new Poster(title);
        poster.setImage(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        poster = repo.insert(poster);
        return ResponseEntity.ok().body("saved in database with id: " + poster.getId());
    }

    public ResponseEntity<byte[]> getById(String id) {
        return composeAnswer(repo.getPosterById(id));
    }

    public ResponseEntity<byte[]> getByTitle(String title) {
        Poster poster = repo.getPosterByTitle(title);
        return composeAnswer(poster);
    }

    private static ResponseEntity<byte[]> composeAnswer(Poster poster) {
        if(poster == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] imageBytes = poster.getImage().getData();
        return ResponseEntity.ok().body(imageBytes);
    }
}
