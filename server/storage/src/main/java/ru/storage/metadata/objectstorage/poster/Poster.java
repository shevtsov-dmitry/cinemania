package ru.storage.metadata.objectstorage.poster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.metadata.ContentMetadata;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Poster {
    @Id
    private String id;
    private String filename;
    private String contentType;
    @DBRef
    @JsonIgnore
    private ContentMetadata contentMetadata;
}
