package ru.storage.content;

import ru.storage.content.objectstorage.poster.Poster;
import ru.storage.content.objectstorage.video.Video;

public record VideoInfoParts(ContentDetails contentDetails, Video video, Poster poster) {
}
