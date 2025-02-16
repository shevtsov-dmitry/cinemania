package ru.storage.content_metadata.video.tv_series;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TvSeriesRepo extends MongoRepository<TvSeries, String> {}
