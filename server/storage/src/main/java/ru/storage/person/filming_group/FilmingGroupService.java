package ru.storage.person.filming_group;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import ru.storage.person.content_creator.ContentCreator;
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
        return filmingGroupRepo.save(filmingGroup);
    }

    public FilmingGroup saveMetadataFromDTO(FilmingGroupDTO dto) {

        ContentCreator director = contentCreatorService.findById(dto.getDirectorId())
                .orElseThrow(() -> new NoSuchElementException());

        List<ContentCreator> actors = dto.getActorsIds().stream()
                .map(id -> contentCreatorService.findById(id)
                        .orElseThrow(() -> new NoSuchElementException()))
                .toList();

        return new FilmingGroup(director, actors);
    }

    public void deleteById(String id) {
        filmingGroupRepo.deleteById(id);
    }
}
