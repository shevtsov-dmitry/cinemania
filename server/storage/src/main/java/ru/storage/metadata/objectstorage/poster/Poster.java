package ru.storage.metadata.objectstorage.poster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.storage.metadata.Content;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Poster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(nullable = false)
    private String filename;
    @NonNull
    @Column(nullable = false)
    private String contentType;
    @JsonIgnore
    @OneToOne
    private Content content;

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
