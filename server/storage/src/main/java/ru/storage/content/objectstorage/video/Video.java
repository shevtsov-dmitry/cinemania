package ru.storage.content.objectstorage.video;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@Document
public class Video {
    @Id
    private String id;
    private String filename;
    private String contentType;
}