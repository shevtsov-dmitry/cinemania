package ru.storage.metadata.objectstorage.exceptions;

/**
 * Indicates invalid usage of predefined request content format
 */
public class ParseRequestIdException extends NumberFormatException {

    private static final String DEFAULT_MESSAGE =
            "Входные данные содержат неверный формат чисел. Следует указать как в примере: \"4,2,592,101,10\".";

    public ParseRequestIdException() {
        super(DEFAULT_MESSAGE);
    }

    public ParseRequestIdException(String message) {
        super(message);
    }
}
