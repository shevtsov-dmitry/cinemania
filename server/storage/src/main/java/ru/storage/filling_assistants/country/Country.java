package ru.storage.filling_assistants.country;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.filling_assistants.base.Nameable;

@Data
@NoArgsConstructor
@Document
public class Country implements Nameable {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;

    @Override
    public String getName() {
        return name;
    }
}
