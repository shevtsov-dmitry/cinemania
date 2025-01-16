package ru.storage.exceptions;

/**
 * Indicates invalid usage of predefined request content format.
 */
public class ParseIdException extends IllegalArgumentException {

    private static final String DEFAULT_MESSAGE =
            "Произошла попытка использования идентификатора представленного в виде неверного формата MongoDB ID.";

    public ParseIdException() {
        super(DEFAULT_MESSAGE);
    }

    public ParseIdException(String message) {
        super(message);
    }
}
