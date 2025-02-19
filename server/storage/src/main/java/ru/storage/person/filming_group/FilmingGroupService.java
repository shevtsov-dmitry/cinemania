package ru.storage.person.filming_group;

import org.springframework.stereotype.Service;
import ru.storage.person.content_creator.ContentCreatorService;

@Service
public class FilmingGroupService {

  private final FilmingGroupRepo filmingGroupRepo;
  private final ContentCreatorService contentCreatorService;

  public FilmingGroupService(
      FilmingGroupRepo filmingGroupRepo, ContentCreatorService contentCreatorService) {
    this.filmingGroupRepo = filmingGroupRepo;
    this.contentCreatorService = contentCreatorService;
  }

  public FilmingGroup saveMetadata(FilmingGroup filmingGroup) {
    var filmingGroupWithDbRefs = new FilmingGroup();
    var actorsFromDb =
        filmingGroup.getActors().stream()
            .map(actor -> contentCreatorService.findById(actor.getId()))
            .toList();
    filmingGroupWithDbRefs.setActors(actorsFromDb);
    var directorFromDb = contentCreatorService.findById(filmingGroup.getDirector().getId());
    filmingGroupWithDbRefs.setDirector(directorFromDb);
    return filmingGroupRepo.save(filmingGroup);
  }

  public void deleteById(String id) {
    filmingGroupRepo.deleteById(id);
  }
}
