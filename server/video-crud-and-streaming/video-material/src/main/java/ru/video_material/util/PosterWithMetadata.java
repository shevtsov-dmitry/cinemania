package ru.video_material.util;

import lombok.Getter;


public class PosterWithMetadata {
        private final String contentId;
        private final byte[] data;

    public PosterWithMetadata(String contentId, byte[] data) {
        this.contentId = contentId;
        this.data = data;
    }

    public String getContentId() {
        return contentId;
    }

    public byte[] getData() {
        return data;
    }
}
