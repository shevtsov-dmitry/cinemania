package ru.storage.content.content_creators;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v0/content-creators")
public class ContentCreatorController {

    private final ContentCreatorService contentCreatorService;

    public ContentCreatorController(ContentCreatorService contentCreatorService) {
        this.contentCreatorService = contentCreatorService;
    }

    @PostMapping
    public ResponseEntity<String> addCreator(@RequestBody ContentCreator creator) {
        final String id = contentCreatorService.addCreator(creator);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PostMapping("user-pic/upload")
    public ResponseEntity<Void> uploadUserPic(@RequestParam String id, @RequestParam MultipartFile multipartFile) {
        contentCreatorService.uploadUserPic(id, multipartFile);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

}
