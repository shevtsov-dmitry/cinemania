package ru.storage.poster;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Poster {
    private String id;
    private Binary image;
}
