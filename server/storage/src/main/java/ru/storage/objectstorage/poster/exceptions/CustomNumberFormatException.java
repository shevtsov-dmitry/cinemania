package ru.storage.objectstorage.poster.exceptions;

/**
 * Indicates invalid usage of predefined request content format
 */
public class CustomNumberFormatException extends NumberFormatException {

    private static final String DEFAULT_MESSAGE =
            "Входные данные содержат неверный формат чисел. Следует указать как в примере: \"4,2,592,101,10\".";

    public CustomNumberFormatException() {
        super(DEFAULT_MESSAGE);
    }

    public CustomNumberFormatException(String message) {
        super(message);
    }
}
