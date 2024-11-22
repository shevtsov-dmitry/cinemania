package ru.storage.metadata.objectstorage.video;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public Video(String name, String contentType) {
        this.name = name;
        this.contentType = contentType;
    }

    //    @JsonIgnore
//    @OneToOne
//    @JoinColumn(name = "content_metadata_id")
//    private ContentMetadata contentMetadata;
}
