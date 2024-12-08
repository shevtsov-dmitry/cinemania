package ru.storage.metadata.objectstorage.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.metadata.ContentMetadata;


@Data
@NoArgsConstructor
@Document
public class Video {
    @Id
    private String id;
    private String name;
    private String contentType;
    @DBRef
    @JsonIgnore
    private ContentMetadata contentMetadata;
}