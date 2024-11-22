package ru.storage.metadata.objectstorage.poster;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Poster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private String contentType;

    public Poster(String filename, String contentType) {
        this.filename = filename;
        this.contentType = contentType;
    }

    //    @NonNull
//    @JsonIgnore
//    @OneToOne
//    @JoinColumn(name = "content_metadata_id")
//    private ContentMetadata contentMetadata;

}
