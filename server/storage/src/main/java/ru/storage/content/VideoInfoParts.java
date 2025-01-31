package ru.storage.content;

import ru.storage.content.poster.Poster;
import ru.storage.content.video.Video;

public record VideoInfoParts(ContentDetails contentDetails, Video video, Poster poster) {
}
