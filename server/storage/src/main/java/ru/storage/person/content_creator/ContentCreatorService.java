package ru.storage.person.content_creator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.fasterxml.jackson.annotation.OptBoolean;
import org.springframework.stereotype.Service;

@Service
public class ContentCreatorService {

  private final ContentCreatorRepo contentCreatorRepo;

  public ContentCreatorService(ContentCreatorRepo contentCreatorRepo) {
    this.contentCreatorRepo = contentCreatorRepo;
  }

  public ContentCreator addCreator(ContentCreator creator) {
    return contentCreatorRepo.save(creator);
  }

  public List<ContentCreator> getAllCreators() {
    return contentCreatorRepo.findAll();
  }

  public Optional<ContentCreator> getCreatorById(String id) {
    return contentCreatorRepo.findById(id);
  }

  public void deleteCreator(String id) {
    contentCreatorRepo.deleteById(id);
  }

  public ContentCreator findCreatorByCountryAndGenre(String country, String genre) {
    // TODO implement the logic to find a creator by country and genre
    //        ContentCreator result = contentCreatorRepo.findByCountryAndGenre(country, genre);
    return null;
  }

  public ContentCreator findById(String id) {
    return contentCreatorRepo
        .findContentCreatorById(id)
        .orElseThrow(() -> new NoSuchElementException("Content Creator not found with ID: " + id));
  }
}
