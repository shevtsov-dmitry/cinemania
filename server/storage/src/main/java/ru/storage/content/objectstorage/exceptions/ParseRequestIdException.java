package ru.storage.content.objectstorage.exceptions;

/**
 * Indicates invalid usage of predefined request content format
 */
public class ParseRequestIdException extends IllegalArgumentException {

    private static final String DEFAULT_MESSAGE =
            "Произошла попытка использования неверного формата идентификатора.";

    public ParseRequestIdException() {
        super(DEFAULT_MESSAGE);
    }

    public ParseRequestIdException(String message) {
        super(message);
    }
}
