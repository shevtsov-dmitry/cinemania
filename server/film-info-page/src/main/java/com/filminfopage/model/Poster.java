package com.filminfopage.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class Poster {
    private String id;
    @NonNull
    private String title;
    @NonNull
    private Binary image;

    public Poster(String title) {
        this.title = title;
    }
}
