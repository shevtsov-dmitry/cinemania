package ru.storage.metadata.objectstorage.poster;

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
    @Column(nullable = false)
    private String filename;
    @NonNull
    @Column(nullable = false)
    private String contentType;
    @JsonIgnore
    @OneToOne
    private ContentMetadata contentMetadata;

}
