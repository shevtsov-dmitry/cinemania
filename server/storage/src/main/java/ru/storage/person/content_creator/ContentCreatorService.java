package ru.storage.person.content_creator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

  public List<ContentCreator> findCreatorByCountryAndGenre(String country, String genre) {
//      return contentCreatorRepo.fin(country, genre);
    // TODO
    throw new RuntimeException("Unimplemented method Exception");
  }

  public List<ContentCreator> findCreatorByFullnamePrefix(String prefix) {
    return contentCreatorRepo.findByFullnameStartingWith(prefix);
  }

  public ContentCreator findById(String id) {
    return contentCreatorRepo
        .findContentCreatorById(id)
        .orElseThrow(() -> new NoSuchElementException("Content Creator not found with ID: " + id));
  }
}
