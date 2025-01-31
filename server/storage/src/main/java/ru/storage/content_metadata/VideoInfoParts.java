package ru.storage.content_metadata;

import ru.storage.content_metadata.poster.Poster;
import ru.storage.content_metadata.video.Video;

public record VideoInfoParts(ContentMetadata contentMetadata, Poster poster, Video video ) {
}
