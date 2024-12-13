package ru.storage.metadata.objectstorage.exceptions;

/**
 * Indicates invalid usage of predefined request content format
 */
public class ParseRequestIdException extends NumberFormatException {

    private static final String DEFAULT_MESSAGE =
            "Произошла попытка использования неверного формата идентификатора.";

    public ParseRequestIdException() {
        super(DEFAULT_MESSAGE);
    }

    public ParseRequestIdException(String message) {
        super(message);
    }
}
