package ru.storage.content_metadata.tv_series;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TvSeriesRepo extends MongoRepository<TvSeries, String> {

}
