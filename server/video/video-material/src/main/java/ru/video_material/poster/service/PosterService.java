package ru.video_material.poster.service;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.poster.model.Poster;
import ru.video_material.poster.repo.PosterRepo;

import java.io.IOException;
import java.util.Objects;

@Service
public class PosterService {

    private final PosterRepo repo;

    @Autowired
    public PosterService(PosterRepo repo) {
        this.repo = repo;
    }

    public ResponseEntity<String> save(MultipartFile file) throws IOException {
        int hash = Objects.requireNonNull(file.getOriginalFilename()).hashCode();
        if (hash == 0) {
            return ResponseEntity.badRequest().body("Couldn't save the video");
        }
        Poster poster = new Poster();
        poster.setImage(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        poster = repo.insert(poster);
        return ResponseEntity.ok(poster.getId());
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
