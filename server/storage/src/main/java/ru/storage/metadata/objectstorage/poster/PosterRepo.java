package ru.storage.metadata.objectstorage.poster;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.storage.metadata.ContentMetadata;

import java.util.Optional;

@Repository
public interface PosterRepo extends MongoRepository<Poster, String> {

//    @Query("{'contentMetadata': ?0}")
//    void updatePosterByContentMetadata(ContentMetadata contentMetadata, String filename, String contentType);

//    @Query("{'contentMetadata.id': ?0}")
//    void deleteByContentMetadataId(Long metadataId);


}
