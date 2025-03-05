package ru.storage.person.filming_group;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
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
    Assert.notNull(filmingGroup, "filmingGroup is null");
    var filmingGroupWithDbRefs = new FilmingGroup();

    var directorFromDb = contentCreatorService.findById(filmingGroup.getDirector().getId());
    filmingGroupWithDbRefs.setDirector(directorFromDb);

    var actorsFromDb =
        filmingGroup.getActors().stream()
            .map(actor -> contentCreatorService.findById(actor.getId()))
            .toList();
    filmingGroupWithDbRefs.setActors(actorsFromDb);

    return filmingGroupRepo.save(filmingGroup);
  }

  public void deleteById(String id) {
    filmingGroupRepo.deleteById(id);
  }
}
