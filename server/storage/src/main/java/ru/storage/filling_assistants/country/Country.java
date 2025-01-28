package ru.storage.filling_assistants.country;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class Country {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    
    public Country(String name) {
        this.name = name;
    }

}
