package ru.storage.metadata;

import ru.storage.metadata.objectstorage.poster.Poster;
import ru.storage.metadata.objectstorage.video.Video;

public record VideoInfoParts(ContentMetadata contentMetadata, Video video, Poster poster) {
}
