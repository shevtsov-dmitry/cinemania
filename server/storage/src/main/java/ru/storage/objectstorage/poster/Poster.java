package ru.storage.objectstorage.poster;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.storage.metadata.ContentMetadata;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Poster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String filename;
    @NonNull
    private String contentType;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "content_metadata_id")
    private ContentMetadata contentMetadata;

//
}
