package ru.storage.content.content_creators;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
public class Actor extends ContentCreator {
    @Id
    private String id;
}
