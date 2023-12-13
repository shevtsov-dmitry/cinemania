package com.videostorage.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.InputStream;

@Document(collection = "videos")
public class Video {
    private String id;
    private String title;
    private String contentType;
    private InputStream stream;

    public Video() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
