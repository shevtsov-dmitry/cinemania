package ru.streaming.model;

public record VideoChunkMetadata(long start, long end, long contentLength, long fileSize) { }
