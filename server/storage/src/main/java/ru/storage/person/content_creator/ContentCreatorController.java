package ru.storage.person.content_creator;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import ru.storage.person.userpic.UserPicService;
import ru.storage.utils.EncodedHttpHeaders;

@RestController
@RequestMapping("api/v0/metadata/content-creators")
public class ContentCreatorController {

  private final ContentCreatorService contentCreatorService;
  private final UserPicService userPicsService;

  public ContentCreatorController(
      ContentCreatorService contentCreatorService, UserPicService userPicsService) {
    this.contentCreatorService = contentCreatorService;
    this.userPicsService = userPicsService;
  }

  @PostMapping
  public ResponseEntity<ContentCreator> addCreator(@RequestBody ContentCreator creator) {
    try {
      return new ResponseEntity<>(contentCreatorService.addCreator(creator), HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(
          null, new EncodedHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("{id}")
  public ResponseEntity<ContentCreator> getCreatorById(@PathVariable String id) {
    return contentCreatorService
        .getCreatorById(id)
        .map(ResponseEntity::ok)
        .orElse(
            new ResponseEntity<>(
                null,
                new EncodedHttpHeaders("Запрашиваемый член съёмочной группы не найден."),
                HttpStatus.NOT_FOUND));
  }

  @GetMapping("country-genre/{country}/{genre}")
  public ResponseEntity<List<ContentCreator>> findCreatorByCountryAndGenre(
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

  @GetMapping("fullname/{prefix}")
  public ResponseEntity<List<ContentCreator>> findCreatorByFullnamePrefix(
      @PathVariable String prefix) {
    prefix = URLDecoder.decode(prefix, StandardCharsets.UTF_8);
    if (prefix.isBlank()) {
      return new ResponseEntity<>(
          null,
          new EncodedHttpHeaders("Необходимо указать имя или фамилию искомого человека."),
          HttpStatus.BAD_REQUEST);
    }

    List<ContentCreator> creators = contentCreatorService.findCreatorByFullnamePrefix(prefix);
    if (!creators.isEmpty()) {
      return ResponseEntity.ok(creators);
    } else {
      return new ResponseEntity<>(
          null,
          new EncodedHttpHeaders("Человек с указанным именем или фамилией не найден."),
          HttpStatus.NOT_FOUND);
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
      userPicsService.deleteById(userPic.getPersonCategory(), List.of(userPic.getId()));
    }
    contentCreatorService.deleteCreator(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
