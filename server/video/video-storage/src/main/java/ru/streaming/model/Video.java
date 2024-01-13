package ru.streaming.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.InputStream;

@Data
@NoArgsConstructor
@Document
public class Video {
    private String title;
    private String contentType;
    private InputStream stream;
}
