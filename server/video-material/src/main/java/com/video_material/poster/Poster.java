<<<<<<<< HEAD:server/video/video-material/src/main/java/ru/video_material/model/Poster.java
package ru.video_material.model;
========
package com.video_material.poster;
>>>>>>>> add-film-form-fix:server/video-material/src/main/java/com/video_material/poster/Poster.java

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class Poster {
    private String id;
    private Binary image;
}

