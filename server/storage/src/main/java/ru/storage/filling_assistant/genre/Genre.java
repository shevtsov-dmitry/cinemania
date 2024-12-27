package ru.storage.filling_assistant.genre;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.filling_assistant.common.Nameable;

@Document
@Data
@NoArgsConstructor
public class Genre implements Nameable {
    @Id
    private Long id;
    @Indexed(unique=true)
    private String name;

    public Genre(String name) {
        this.name = name;
    }

}
