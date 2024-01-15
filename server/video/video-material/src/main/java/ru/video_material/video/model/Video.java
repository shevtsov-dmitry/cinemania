package ru.video_material.video.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.InputStream;

@Data
@NoArgsConstructor
@Document
public class Video {
    private String id;
    private InputStream stream;
    private String contentType;
}
