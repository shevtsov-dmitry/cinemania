package ru.storage.filling_assistants.genre;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document
public class Genre {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;

    public Genre(String name) {
        this.name = name;
    }
}
