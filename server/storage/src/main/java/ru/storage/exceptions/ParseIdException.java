package ru.storage.exceptions;

/**
 * Indicates invalid usage of predefined request content format.
 */
public class ParseIdException extends IllegalArgumentException {

    public static final String DEFAULT_ERROR_MESSAGE =
            "Произошла попытка использования идентификатора представленного в виде неверного формата ID.";
    
    public ParseIdException() {
        super(DEFAULT_ERROR_MESSAGE);
    }

    public ParseIdException(String message) {
        super(message);
    }
}
