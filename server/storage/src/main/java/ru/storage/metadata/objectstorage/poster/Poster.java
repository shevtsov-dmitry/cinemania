package ru.storage.metadata.objectstorage.poster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Poster {
    @Id
    private String id;
    @JsonIgnore
    private String filename;
    private String contentType;
}
