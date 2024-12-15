package ru.storage.content;

import ru.storage.content.poster.PosterMetadata;
import ru.storage.content.video.VideoMetadata;

public record VideoInfoParts(ContentDetails contentDetails, VideoMetadata videoMetadata, PosterMetadata posterMetadata) {
}
