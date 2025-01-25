package ru.storage.content_creators;

import java.util.List;

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

    public ContentCreator getCreatorById(String id) {
        return contentCreatorRepo.findById(id).orElse(null);
    }
    
    public void deleteCreator(String id) {
        contentCreatorRepo.deleteById(id);
    }
}
