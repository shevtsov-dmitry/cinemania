package ru.video_material.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.VideoMetadata;
import ru.video_material.repo.MetadataRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static java.lang.StringTemplate.STR;

@Service
public class VideoService {

    private final MetadataRepo metadataRepo;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations operations;


    @Autowired
    public VideoService(MetadataRepo metadataRepo, GridFsTemplate gridFsTemplate, GridFsOperations operations) {
        this.metadataRepo = metadataRepo;
        this.gridFsTemplate = gridFsTemplate;
        this.operations = operations;
    }

    // ### VIDEO
    public String saveVideo(MultipartFile file) throws IOException {
        ObjectId savedFileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                Objects.requireNonNull(file.getContentType())
        );
        return savedFileId.toString();
    }
    public byte[] downloadVideoById(String id) throws IOException {
        GridFSFile foundFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        Assert.notNull(foundFile, "Video-material service. File is null in downloadVideoById(String id) when try to retrieve it from database.");
        var outputStream = new ByteArrayOutputStream();
        operations.getResource(Objects.requireNonNull(foundFile)).getInputStream().transferTo(outputStream);
        return outputStream.toByteArray();
    }

    public String deleteVideoById(String id) {
        final Query query = new Query(Criteria.where("_id").is(id));
        gridFsTemplate.delete(query);
        if (gridFsTemplate.findOne(query) != null) {
            throw new IllegalArgumentException(STR."Deletion failed. The video with id \{id} doesn't exist in GridFS.");
        }
        return id;
    }

    // ### METADATA
    public ResponseEntity<String> saveMetadata(VideoMetadata videoMetadata) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        System.out.println(videoMetadata);
        final String id = metadataRepo.save(videoMetadata).getId();
        return new ResponseEntity<>(id, httpHeaders, HttpStatus.OK);
    }

    public ResponseEntity<List<VideoMetadata>> getMetadataByTitle(String title) {
        List<VideoMetadata> occurrences = metadataRepo.getByTitle(title);
        if (occurrences == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(occurrences);
    }

    public ResponseEntity<VideoMetadata> getMetadataById(String id) {
        VideoMetadata metadata = metadataRepo.getById(id);
        if (metadata == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(metadata);
    }

    public ResponseEntity<String> deleteMetadataById(String id) {
        if (!metadataRepo.existsById(id)) {
            return ResponseEntity.badRequest().body(STR."Couldn't find video with id \{id}.");
        }
        return ResponseEntity.ok("");
    }


}
