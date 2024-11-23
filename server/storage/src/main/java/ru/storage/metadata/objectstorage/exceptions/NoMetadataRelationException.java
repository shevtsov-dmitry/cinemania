package ru.storage.metadata.objectstorage.exceptions;

import org.springframework.dao.InvalidDataAccessApiUsageException;

/**
 * Happens when trying to save new poster image without existing metadata instance, which can lead to database cluttering
 */
public class NoMetadataRelationException extends InvalidDataAccessApiUsageException {

    public static final String ERROR_MESSAGE ="Метод сохранения плаката не предназначен для работы без ссылки на таблицу метаданных.";

    public NoMetadataRelationException() {
        super(ERROR_MESSAGE);
    }

    public NoMetadataRelationException(String msg) {
        super(msg);
    }
}
