package ru.storage.person.filming_group;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.person.content_creator.ContentCreator;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmingGroup {
    @Id
    private String id;
    @DBRef
    private ContentCreator director;
    @DBRef
    private List<ContentCreator> actors;
    private List<ContentCreator> operators;
    private List<ContentCreator> directors;

    public FilmingGroup(ContentCreator director, List<ContentCreator> actors) {
        this.director = director;
        this.actors = actors;
    }

    public FilmingGroup(ContentCreator director, List<ContentCreator> actors, List<ContentCreator> operators, List<ContentCreator> directors) {
        this.director = director;
        this.actors = actors;
        this.operators = operators;
        this.directors = directors;
    }

}
