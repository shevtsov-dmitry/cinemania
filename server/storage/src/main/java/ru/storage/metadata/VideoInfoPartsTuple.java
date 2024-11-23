package ru.storage.metadata;

import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.video.Video;

public record VideoInfoPartsTuple(ContentMetadata contentMetadata, Video video, Poster poster) {
}
