package ru.storage.content_creators;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ContentCreatorService {

    private final ContentCreatorRepo contentCreatorRepository;

    public ContentCreatorService(ContentCreatorRepo contentCreatorRepository) {
        this.contentCreatorRepository = contentCreatorRepository;
    }
    

    public String addCreator(ContentCreator creator) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addCreator'");
    }
     

    public List<ContentCreator> getAllCreators() {
        return contentCreatorRepository.findAll();
    }

    public ContentCreator getCreatorById(String id) {
        return contentCreatorRepository.findById(id).orElse(null);
    }
    
    public void deleteCreator(String id) {
        contentCreatorRepository.deleteById(id);
    }
}
