package ru.storage.objectstorage.poster;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.storage.metadata.MetadataRepo;

@Service
public class PosterService {

	private final PosterRepo posterRepo;
	private final MetadataRepo metadataRepo;

	// TODO use S3 instead
	private final String IMAGES_STORAGE_PATH = "/home/shd/Pictures/tmp/posters";
	private static final Logger log = LoggerFactory.getLogger(PosterService.class);

	public PosterService(PosterRepo posterRepo, MetadataRepo metadataRepo) {
		this.posterRepo = posterRepo;
		this.metadataRepo = metadataRepo;
	}

	public Poster savePoster(Long metadataId, MultipartFile file) {
		var savedPoster = posterRepo.save(new Poster(file.getName(), file.getContentType()));
		return savedPoster;
		// TODO save in actual S3
		// log.warn("Не удалось сохранить файл плаката с именем {} из-за {}",
	}

	public List<byte[]> getImagesByContentMetadataId(String contentMetadataIds) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateExistingImage(Long metadataId, MultipartFile image) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updateExistingImage'");
	}

	public void deleteByIds(String contentMetadataIds) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'deleteByIds'");
	}

	// public List<ContentMetadata> queryMetadataRepoForIds(int amount) {
	// final Pageable requestedAmountRestriction = PageRequest.of(0, amount);
	// return metadataRepo.findRecentlyAdded(requestedAmountRestriction).stream()
	// .map(ContentMetadata::getPosterId)
	// .toList();
	// }
	//
	// public List<Map<String, byte[]>> getRecentlySavedPosters(int amount) {
	// List<String> recentSavedPosterIds = queryMetadataRepoForIds(amount);
	// List<Map<String, byte[]>> imagesAndMetadata = new ArrayList<>(amount);
	//
	// for (String id : recentSavedPosterIds) {
	// Map<String, byte[]> data = new HashMap<>();
	//
	// Poster poster = posterRepo.findById(id);
	// final ContentMetadata metadata = metadataRepo.getByPosterId(poster.getId());
	// data.put("metadataId", metadata.getId().getBytes());
	// data.put("title", metadata.getTitle().getBytes());
	// data.put("releaseDate", metadata.getReleaseDate().getBytes());
	// data.put("country", metadata.getCountry().getBytes());
	// data.put("mainGenre", metadata.getMainGenre().getBytes());
	// data.put("subGenres", metadata.getSubGenres().toString().replace("[",
	// "").replace("]", "").getBytes());
	// data.put("age", metadata.getAge().toString().getBytes());
	// data.put("rating", String.valueOf(metadata.getRating()).getBytes());
	// data.put("poster", poster.getImage().getData());
	// data.put("videoId", metadata.getVideoId().getBytes());
	//
	// imagesAndMetadata.add(data);
	// }
	// return imagesAndMetadata;
	// }

}
