package ru.storage.person.content_creator;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.storage.person.userpic.UserPic;
import ru.storage.person.userpic.UserPicsService;
import ru.storage.utils.EncodedHttpHeaders;

@RestController
@RequestMapping("api/v0/metadata/content-creators")
public class ContentCreatorController {

  private final ContentCreatorService contentCreatorService;
  private final UserPicsService userPicsService;

  public ContentCreatorController(
      ContentCreatorService contentCreatorService, UserPicsService userPicsService) {
    this.contentCreatorService = contentCreatorService;
    this.userPicsService = userPicsService;
  }

  @PostMapping
  public ResponseEntity<ContentCreator> addCreator(@RequestBody ContentCreator creator) {
    return new ResponseEntity<>(contentCreatorService.addCreator(creator), HttpStatus.CREATED);
  }

  @GetMapping("{id}")
  public ResponseEntity<ContentCreator> getCreatorById(@PathVariable String id) {
    var creator = contentCreatorService.getCreatorById(id);
    if (creator.isPresent()) {
      return new ResponseEntity<>(creator.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(
          null,
          new EncodedHttpHeaders("Запрашиваемый член съёмочной группы не найден."),
          HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("{country}/{genre}")
  public ResponseEntity<ContentCreator> findCreatorByCountryAndGenre(
      @Nullable @PathVariable String country, @Nullable @PathVariable String genre) {
    if ((country == null || country.isBlank()) && (genre == null || genre.isBlank())) {
      return new ResponseEntity<>(
          null,
          new EncodedHttpHeaders(
              "Необходимо выбрать хотя бы один параметр. Например, country=Russia или"
                  + " genre=Action."),
          HttpStatus.BAD_REQUEST);
    }

    try {
      return ResponseEntity.ok(contentCreatorService.findCreatorByCountryAndGenre(country, genre));
    } catch (Exception e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders("Совпадений не найдено."), HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("all")
  public ResponseEntity<List<ContentCreator>> getAllCreators() {
    return new ResponseEntity<>(contentCreatorService.getAllCreators(), HttpStatus.OK);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> deleteCreator(@PathVariable String id) {
    var contentCreatorOptional = contentCreatorService.getCreatorById(id);
    if (contentCreatorOptional.isEmpty()) {
      return new ResponseEntity<>(
          null,
          new EncodedHttpHeaders("Запрашиваемый член съёмочной группы не найден."),
          HttpStatus.NOT_FOUND);
    }
    var userPic = contentCreatorOptional.get().getUserPic();
    if (userPic != null) {
      userPicsService.delete(userPic.getPersonCategory(), userPic.getId());
    }
    contentCreatorService.deleteCreator(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
