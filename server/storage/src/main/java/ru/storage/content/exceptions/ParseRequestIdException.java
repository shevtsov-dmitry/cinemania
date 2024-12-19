package ru.storage.content.exceptions;

/**
 * Indicates invalid usage of predefined request content format
 */
public class ParseRequestIdException extends IllegalArgumentException {

    private static final String DEFAULT_MESSAGE =
            "Произошла попытка использования идентификатора представленного в виде неверного формата MongoDB ID.";

    public ParseRequestIdException() {
        super(DEFAULT_MESSAGE);
    }

    public ParseRequestIdException(String message) {
        super(message);
    }
}
