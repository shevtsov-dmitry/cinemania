package ru.storage.filling_assistant.country;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.filling_assistant.common.Nameable;

@Document
@Data
@NoArgsConstructor
public class Country implements Nameable {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;

    public Country(String name) {
        this.name = name;
    }
}
