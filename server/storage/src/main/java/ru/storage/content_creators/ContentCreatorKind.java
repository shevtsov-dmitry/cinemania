package ru.storage.content_creators;

import lombok.Getter;

@Getter
public enum ContentCreatorKind {
    DIRECTOR("DIRECTOR"),
    ACTOR("ACTOR");

    private final String name;

    private ContentCreatorKind(String name) {
        this.name = name;
    }

}
