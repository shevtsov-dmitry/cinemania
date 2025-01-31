package ru.storage.content_metadata.poster;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterRepo extends MongoRepository<Poster, String> {

//    @Query("{'contentMetadata': ?0}")
//    void updatePosterByContentMetadata(ContentMetadata contentMetadata, String filename, String contentType);

//    @Query("{'contentMetadata.id': ?0}")
//    void deleteByContentMetadataId(Long metadataId);

}
