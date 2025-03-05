package ru.storage.content_metadata.video.episode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Episode {

    @Id
    private String id;
    private String filename;
    private String contentType;
    private Integer season;
    private Integer episode;
    private Long size;

    public Episode(String filename, String contentType, Integer season, Integer episode, Long size) {
        this.filename = filename;
        this.contentType = contentType;
        this.season = season;
        this.episode = episode;
        this.size = size;
    }
}
