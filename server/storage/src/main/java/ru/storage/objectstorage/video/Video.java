package ru.storage.objectstorage.video;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.storage.metadata.ContentMetadata;

/**
 * Video
 */
@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String contentType;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "content_metadata_id")
    private ContentMetadata contentMetadata;
}
