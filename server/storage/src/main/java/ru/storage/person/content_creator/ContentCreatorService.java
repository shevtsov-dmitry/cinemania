package ru.storage.person.content_creator;

import java.util.*;

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
    throw new RuntimeException("Unimplemented method Exception");
  }

  public List<ContentCreator> findCreatorByFullnamePrefix(String prefix) {
    Set<ContentCreator> contentCreators = new HashSet<>();
    if (isEnglishLetter(prefix.charAt(0))) {
      contentCreators.addAll(contentCreatorRepo.findByNameLatinStartingWith(prefix));
      contentCreators.addAll(contentCreatorRepo.findBySurnameLatinStartingWith(prefix));
    } else {
      contentCreators.addAll(contentCreatorRepo.findByNameStartingWith(prefix));
      contentCreators.addAll(contentCreatorRepo.findBySurnameStartingWith(prefix));
    }
    return contentCreators.stream().toList();
  }

  private static boolean isEnglishLetter(char c) {
    return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
  }

  public ContentCreator findById(String id) {
    return contentCreatorRepo
        .findContentCreatorById(id)
        .orElseThrow(() -> new NoSuchElementException("Content Creator not found with ID: " + id));
  }
}
