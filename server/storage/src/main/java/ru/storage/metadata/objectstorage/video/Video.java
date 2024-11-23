package ru.storage.metadata.objectstorage.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.storage.metadata.ContentMetadata;

/**
 * Video
 */
@Entity
@Data
@NoArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String contentType;
    @OneToOne
    @JsonIgnore
    private ContentMetadata contentMetadata;

}
