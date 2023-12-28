package com.filminfopage.model;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Poster {
    private String id;
    private String title;
    private Binary image;
}
