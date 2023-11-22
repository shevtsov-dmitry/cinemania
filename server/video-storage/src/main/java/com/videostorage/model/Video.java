package com.videostorage.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "videos")
public class Video {
    private String id;
    private String filename;
    private String contentType;

    public Video() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
