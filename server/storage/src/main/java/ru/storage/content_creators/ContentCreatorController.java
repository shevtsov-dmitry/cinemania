package ru.storage.content_creators;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.storage.userpic.UserPicsService;

@RestController
@RequestMapping("api/v0/content-creators")
public class ContentCreatorController {

    private final ContentCreatorService contentCreatorService;
    private final UserPicsService userPicsService;

    public ContentCreatorController(ContentCreatorService contentCreatorService, UserPicsService userPicsService) {
        this.contentCreatorService = contentCreatorService;
        this.userPicsService = userPicsService;
    }
    
    @PostMapping
    public ResponseEntity<String> addCreator(@RequestBody ContentCreator creator) {
        final String id = contentCreatorService.addCreator(creator);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<ContentCreator> getCreatorById(@PathVariable String id) {
        return new ResponseEntity<>(contentCreatorService.getCreatorById(id), HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<List<ContentCreator>> getAllCreators() {
        return new ResponseEntity<>(contentCreatorService.getAllCreators(), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCreator(@PathVariable String id) {
        contentCreatorService.deleteCreator(id);
        var userPic = contentCreatorService.getCreatorById(id).getUserPic();
        userPicsService.delete(userPic.getPicCategory(), userPic.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
